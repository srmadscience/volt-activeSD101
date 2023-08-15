
/* This file is part of VoltDB.
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

import java.util.Date;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class SummarizeUniqueEvents extends VoltProcedure {

  // @formatter:off

    public static final SQLStmt getEvent = new SQLStmt("SELECT * FROM events_pk WHERE user_id = ? AND session_id = ?;");
    
    public static final SQLStmt getTotals = new SQLStmt("SELECT total_value FROM events_totals WHERE user_id = ? AND session_id = ?;");

    public static final SQLStmt recordEvent = new SQLStmt("INSERT INTO events_pk (user_id,session_id,insert_date) VALUES (?,?, ?);");

    public static final SQLStmt upsertTotals = new SQLStmt("UPSERT INTO events_totals (user_id,session_id,last_written,event_value) VALUES (?,?,?,?);");

    public static final SQLStmt forwardToKafka = new SQLStmt("INSERT INTO unique_events (user_id,session_id,insert_date,event_value) VALUES (?,?,?,?);");


    // @formatter:on

    public VoltTable[] run(long userId, long sessionId, Date eventDate, long eventValue) throws VoltAbortException {

        voltQueueSQL(getEvent, userId, sessionId);
        voltQueueSQL(getTotals, userId, sessionId);

        VoltTable[] eventRecord = voltExecuteSQL();
        
        long updatedEventValue = eventValue;

        // Sanity check: Does this session exist?
        if (!eventRecord[0].advanceRow()) {
            return new VoltTable[0];
        }

        if (eventRecord[1].advanceRow()) {
            updatedEventValue += eventRecord[1].getLong("total_value");
        }

        voltQueueSQL(recordEvent, userId, sessionId, eventDate);
        
        if (updatedEventValue > 100) {
            voltQueueSQL(upsertTotals, userId, sessionId, eventDate,0);
            voltQueueSQL(forwardToKafka, userId, sessionId, eventDate,updatedEventValue);
        }
 
        return voltExecuteSQL(true);

    }
}
