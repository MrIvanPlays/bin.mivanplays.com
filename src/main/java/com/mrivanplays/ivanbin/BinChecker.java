/*
    Copyright (c) 2019 Ivan Pekov
    Copyright (c) 2019 Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.mrivanplays.ivanbin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class BinChecker implements Runnable
{

    @Override
    public void run()
    {
        File[] files = BinBootstrap.binsDirectory.listFiles(($, name) -> name.endsWith(".json"));
        if (files == null)
        {
            return;
        }
        List<SimpleBin> toDelete = new ArrayList<>();
        for (File jsonFiles : files)
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(jsonFiles)))
            {
                JSONObject object = new JSONObject(reader.lines().collect(Collectors.joining()));
                TemporalAccessor createdAt = DateTimeFormatter.RFC_1123_DATE_TIME.parse(object.getString("createdAt"));
                Duration duration = Duration.between(OffsetDateTime.from(createdAt), OffsetDateTime.now());
                if (duration.toHours() >= 72)
                {
                    toDelete.add(new SimpleBin(jsonFiles, object.getString("binId")));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (!toDelete.isEmpty())
        {
            for (SimpleBin bin : toDelete)
            {
                File file = new File(BinBootstrap.binsDirectory, bin.binId + ".txt");
                file.delete();
                bin.jsonFile.delete();
            }
            toDelete.clear();
        }
        // why calling a system gc?
        // the runnable have made lots of things that are going to be held into the system for a long
        // time, until the jvm realize that it doesn't need them. This way the program is freeing up some
        // memory.
        System.gc();
    }

    private static class SimpleBin
    {

        File jsonFile;
        String binId;

        SimpleBin(File jsonFile, String binId)
        {
            this.jsonFile = jsonFile;
            this.binId = binId;
        }
    }
}
