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

import com.mrivanplays.ivanbin.handlers.get.BinReader;
import com.mrivanplays.ivanbin.handlers.get.FaviconRoute;
import com.mrivanplays.ivanbin.handlers.get.HTMLRenderingRoute;
import com.mrivanplays.ivanbin.handlers.post.BinCreate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import spark.Route;
import static spark.Spark.get;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.post;

public class BinBootstrap
{

    public static File notFound;
    public static File reader;
    public static File binsDirectory;

    static
    {
        notFound = new File("/usr/share/nginx/bin/not-found.html");
        reader = new File("/usr/share/nginx/bin/reader.html");
        binsDirectory = new File("/usr/share/nginx/bin/bins/");
        if (!binsDirectory.exists())
        {
            binsDirectory.mkdirs();
        }
    }

    public static void main(String[] args)
    {
        port(6869);
        initExceptionHandler(Throwable::printStackTrace);

        Route notFoundErr = new HTMLRenderingRoute(notFound);
        notFound(notFoundErr);

        Route favicon = new FaviconRoute();
        get("/favicon.ico", favicon);

        Route normalBin = new HTMLRenderingRoute(new File("/usr/share/nginx/bin/default-page.html"));
        get("/", normalBin);
        Route binReader = new BinReader();
        get("/:id", binReader);

        Route binCreate = new BinCreate(binsDirectory);
        post("/backend/create", "text", binCreate);
    }

    public static String inlineHTML(File file) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            return inline(reader.lines().collect(Collectors.toList()));
        }
    }

    public static String inline(List<String> list)
    {
        StringBuilder bean = new StringBuilder();
        for (String line : list)
        {
            bean.append(line).append("\n");
        }
        return bean.toString();
    }

    public static String generateRandomString()
    {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++)
        {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
