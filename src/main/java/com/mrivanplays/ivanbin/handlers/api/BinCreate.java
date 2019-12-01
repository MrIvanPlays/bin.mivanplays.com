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
package com.mrivanplays.ivanbin.handlers.api;

import com.mrivanplays.ivanbin.BinBootstrap;
import com.mrivanplays.ivanbin.utils.StringUtils;
import java.io.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinCreate implements Route
{

    private File binsDirectory;

    public BinCreate()
    {
        this.binsDirectory = BinBootstrap.binsDirectory;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception
    {
        response.type("application/json");
        String code = request.body();
        if (code == null || code.isEmpty())
        {
            response.status(403);
            return "{\"error\": \"403 forbidden (empty/null string for code)\"";
        }
        String existing = checkForExisting(code);
        if (existing != null)
        {
            response.status(200);
            return "{\"binId\": \"" + existing + "\"}";
        }
        String binString = StringUtils.generateRandomString(11);
        File file = new File(binsDirectory, binString + ".txt");
        if (file.exists())
        {
            binString = StringUtils.generateRandomString(11);
            file = new File(binsDirectory, binString + ".txt");
        }
        file.createNewFile();
        try (Writer writer = new FileWriter(file))
        {
            writer.write(code);
        }
        String createdAt = OffsetDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        String expiresAt = OffsetDateTime.now().plusHours(72).format(DateTimeFormatter.RFC_1123_DATE_TIME);
        JSONObject dataObject = new JSONObject();
        dataObject.put("binId", binString);
        dataObject.put("createdAt", createdAt);
        dataObject.put("expiresAt", expiresAt);
        dataObject.put("body", code);
        File jsonData = new File(binsDirectory, binString + ".json");
        jsonData.createNewFile();
        try (Writer writer = new FileWriter(jsonData))
        {
            writer.write(dataObject.toString());
        }
        response.status(200);
        return "{\"binId\": \"" + binString + "\"}";
    }

    private String checkForExisting(String code)
    {
        File[] files = BinBootstrap.binsDirectory.listFiles(($, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0)
        {
            return null;
        }
        for (File file : files)
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(file)))
            {
                String readCode = reader.lines().collect(BinBootstrap.newLineCollector);
                if (StringUtils.equals(code, readCode))
                {
                    return file.getName().replace(".txt", "");
                }
            }
            catch (IOException ignored)
            {
            }
        }
        return null;
    }
}
