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
package com.mrivanplays.ivanbin.handlers.post;

import com.mrivanplays.ivanbin.BinBootstrap;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinCreate implements Route
{

    private File binsDirectory;

    public BinCreate(File binsDirectory)
    {
        this.binsDirectory = binsDirectory;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception
    {
        String code = request.body();
        String binString = BinBootstrap.generateRandomString();
        File file = new File(binsDirectory, binString + ".txt");
        if (file.exists())
        {
            binString = BinBootstrap.generateRandomString();
            file = new File(binsDirectory, binString + ".txt");
        }
        file.createNewFile();
        try (Writer writer = new FileWriter(file))
        {
            writer.write(code);
        }
        response.type("application/json");
        response.status(200);
        JSONObject object = new JSONObject();
        object.put("binId", binString);
        return object;
    }
}
