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

import com.mrivanplays.ivanbin.handlers.BinReaderRaw;
import com.mrivanplays.ivanbin.handlers.api.BinInfo;
import com.mrivanplays.ivanbin.handlers.BinReader;
import com.mrivanplays.ivanbin.handlers.FaviconRoute;
import com.mrivanplays.ivanbin.handlers.api.BinCreate;
import com.mrivanplays.ivanbin.utils.Bin;
import com.mrivanplays.ivanbin.utils.NewLineCollector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.json.JSONObject;
import spark.Route;
import static spark.Spark.get;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.post;

public class BinBootstrap
{

    public static String notFoundHTML;
    public static String readerHTML;
    public static File binsDirectory;
    public static Collector<CharSequence, StringBuilder, String> newLineCollector;

    static
    {
        File notFound = new File("/usr/share/nginx/bin/not-found.html");
        File readerFile = new File("/usr/share/nginx/bin/reader.html");
        newLineCollector = new NewLineCollector();

        try (BufferedReader notFoundReader = new BufferedReader(new FileReader(notFound)))
        {
            notFoundHTML = notFoundReader.lines().collect(Collectors.joining());
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try (BufferedReader readerFileReader = new BufferedReader(new FileReader(readerFile)))
        {
            readerHTML = readerFileReader.lines().collect(Collectors.joining());
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        binsDirectory = new File("/usr/share/nginx/bin/bins/");
        if (!binsDirectory.exists())
        {
            binsDirectory.mkdirs();
        }
    }

    public static void main(String[] args) throws Exception
    {
        port(6869);
        initExceptionHandler(Throwable::printStackTrace);

        Route notFoundErr = htmlRenderingRoute(notFoundHTML);
        notFound(notFoundErr);

        Route favicon = new FaviconRoute();
        get("/favicon.ico", favicon);

        String defaultPageHTMLCache;

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("/usr/share/nginx/bin/default-page.html"))))
        {
            defaultPageHTMLCache = reader.lines().collect(Collectors.joining());
        }

        Route normalBin = htmlRenderingRoute(defaultPageHTMLCache);
        get("/", normalBin);
        Route binReader = new BinReader();
        get("/:id", binReader);
        Route binReaderRaw = new BinReaderRaw();
        get("/raw/:id", binReaderRaw);

        // api
        Route binCreate = new BinCreate();
        post("/api/create", "text", binCreate);
        post("/api/create/", "text", binCreate);

        Route binInfo = new BinInfo();
        get("/api/info/:id", binInfo);
        get("/api/info/:id/", binInfo);

        BinChecker binChecker = new BinChecker();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(binChecker, 1, 1, TimeUnit.HOURS);
    }

    public static Optional<Bin> getBin(String id) throws IOException
    {
        File binFile = new File(binsDirectory, id + ".txt");
        if (binFile.exists())
        {
            File jsonDataFile = new File(binsDirectory, id + ".json");
            try (BufferedReader reader = new BufferedReader(new FileReader(jsonDataFile)))
            {
                return Optional.of(new Bin(new JSONObject(reader.readLine()), binFile, jsonDataFile));
            }
        }
        else
        {
            return Optional.empty();
        }
    }

    private static Route htmlRenderingRoute(String html)
    {
        return (request, response) ->
        {
            response.type("text/html");
            response.status(200);
            return html;
        };
    }
}
