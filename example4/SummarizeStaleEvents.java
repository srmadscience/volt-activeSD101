
/* This file is part of Volt Active Data.
 * Copyright (C) 2008-2023 Volt Active Data Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.types.TimestampType;

public class SummarizeStaleEvents extends VoltProcedure {

  // @formatter:off

    public static final SQLStmt getOldestStaleSession = new SQLStmt("SELECT MIN(stale_date) stale_date "
            + "FROM user_totals WHERE stale_date < NOW;");

    public static final SQLStmt exportStaleSessions = new SQLStmt("INSERT INTO summarized_events_by_user "
            + " (user_id,insert_date,event_value,reason) "
            + "SELECT user_id, last_written, total_value, 'S' reason "
            + "FROM user_totals "
            + "WHERE stale_date = ?"
            + "ORDER BY user_id;");

    public static final SQLStmt updateStaleSessionTotals = new SQLStmt("UPDATE user_totals "
            + "SET total_value = 0 "
            + "  , stale_date = null "
            + "WHERE stale_date = ?;");

    // @formatter:on

    public VoltTable[] run() throws VoltAbortException {

        voltQueueSQL(getOldestStaleSession);
        VoltTable staleRecord = voltExecuteSQL()[0];

        if (staleRecord.advanceRow()) {
            TimestampType oldestStaleRecordDate = staleRecord.getTimestampAsTimestamp("stale_date");

            if (!staleRecord.wasNull()) {
                voltQueueSQL(exportStaleSessions, oldestStaleRecordDate);
                voltQueueSQL(updateStaleSessionTotals, oldestStaleRecordDate);
            }
        }

        return voltExecuteSQL(true);

    }
}
