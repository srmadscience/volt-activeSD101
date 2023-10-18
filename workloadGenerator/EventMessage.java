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

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventMessage {

    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int userid;
    int sessionId;
    int value;
    Date eventTime;

    public EventMessage(int userid, int sessionId, Date eventTime, int value) {

        super();
        this.userid = userid;
        this.sessionId = sessionId;
        this.eventTime = eventTime;
        this.value = value;
    }

    public EventMessage(String commaDelimitedMessage) throws Exception {

        String[] elements = commaDelimitedMessage.split(",");

        if (elements.length != 4) {
            throw new Exception("Must have 4 fields");
        }

        userid = Integer.parseInt(elements[0]);
        sessionId = Integer.parseInt(elements[1]);
        eventTime = sdfDate.parse(elements[2]);
        value = Integer.parseInt(elements[3]);

    }

    /**
     * @return this object in a kafka friendly format, using userid as a key and : as a key.separator
     */
    public String toKafkaCsvString() {

        StringBuilder builder = new StringBuilder();
        builder.append(userid);
        builder.append(":");
        builder.append(userid);
        builder.append(",");
        builder.append(sessionId);
        builder.append(",");
        builder.append(sdfDate.format(eventTime));
        builder.append(",");
        builder.append(value);

        return builder.toString();
    }

   
    /**
     * @return the userid
     */
    public int getUserid() {
        return userid;
    }

   
}
