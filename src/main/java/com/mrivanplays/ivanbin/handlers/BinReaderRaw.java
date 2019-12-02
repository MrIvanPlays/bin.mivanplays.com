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
package com.mrivanplays.ivanbin.handlers;

import com.mrivanplays.ivanbin.BinBootstrap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinReaderRaw implements Route
{

    @Override
    public Object handle(Request request, Response response) throws Exception
    {
        String id = request.params(":id");
        File file = new File(BinBootstrap.binsDirectory, id + ".txt");
        if (!file.exists())
        {
            response.type("text/html");
            response.status(404);

            return BinBootstrap.notFoundHTML;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            response.type("text");
            response.status(200);

            return reader.lines().collect(BinBootstrap.newLineCollector)
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }
    }
}
