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
import com.mrivanplays.ivanbin.utils.RandomStringGenerator;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
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
        String code = request.body();
        String binString = RandomStringGenerator.generate(11);
        File file = new File(binsDirectory, binString + ".txt");
        if (file.exists())
        {
            binString = RandomStringGenerator.generate(11);
            file = new File(binsDirectory, binString + ".txt");
        }
        file.createNewFile();
        try (Writer writer = new FileWriter(file))
        {
            writer.write(code);
        }
        String createdAt = OffsetDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        JSONObject dataObject = new JSONObject();
        dataObject.put("binId", binString);
        dataObject.put("createdAt", createdAt);
        File jsonData = new File(binsDirectory, binString + ".json");
        jsonData.createNewFile();
        try (Writer writer = new FileWriter(jsonData))
        {
            writer.write(dataObject.toString());
        }
        response.type("application/json");
        response.status(200);
        return "{\"binId\": \"" + binString + "\"}";
    }
}
