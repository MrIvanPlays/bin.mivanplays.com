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
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinReader implements Route
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
            response.type("text/html");
            response.status(200);

            List<String> lines = reader.lines().collect(Collectors.toList());

            StringBuilder lineNumbers = new StringBuilder();

            for (int i = 0; i < lines.size(); i++)
            {
                lineNumbers.append((i + 1)).append("<br>");
            }

            String codeInline = collect(lines).replace("<", "&lt;").replace(">", "&gt;");

            return BinBootstrap.readerHTML.replace("{code_here}", codeInline).replace("{lch}", lineNumbers);
        }
    }

    private String collect(List<String> lines)
    {
        Collector<CharSequence, StringBuilder, String> newLineCollector = BinBootstrap.newLineCollector;
        StringBuilder builder = newLineCollector.supplier().get();
        for (String line : lines)
        {
            newLineCollector.accumulator().accept(builder, line);
        }
        return newLineCollector.finisher().apply(builder);
    }
}
