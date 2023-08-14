import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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

public class GenerateActiveSD101Workload {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: GenerateActiveSD101Workload duration_in_seconds messagesPerSecond");
            System.exit(1);
        }

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final int maxUserid = 1000;
        final int maxSessionId = 1000000;
        final int maxValue = 100;
        final int dupFrequency = 2;

        final int durationInSeconds = Integer.parseInt(args[0]);
        final int messagePerSecond = Integer.parseInt(args[1]);

        final long endTime = System.currentTimeMillis() + (1000 * durationInSeconds);

        Random r = new Random();

        while (System.currentTimeMillis() < endTime) {

            for (int i = 0; i < messagePerSecond; i++) {

                Date now = new Date();
                String strDate = sdfDate.format(now);

                int thisUserId = r.nextInt(maxUserid);
                int thisSessionId = r.nextInt(maxSessionId);
                int thisAmount = r.nextInt(maxValue);
                
                printEvent(thisUserId, thisSessionId, strDate, thisAmount);

                // Generate duplicate events every now and then...
                if (r.nextInt(dupFrequency) == 0) {
                    printEvent(thisUserId, thisSessionId, strDate, thisAmount);

                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    private static void printEvent(int thisUserId, int thisSessionId, String strDate, int thisAmount) {
        System.out.println(thisUserId + "," + thisSessionId + "," + strDate + "," + thisAmount);

    }

}
