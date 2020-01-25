package com.mrivanplays.ivanbin;

import static spark.Spark.get;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.post;

import com.mrivanplays.ivanbin.handlers.BinReader;
import com.mrivanplays.ivanbin.handlers.BinReaderRaw;
import com.mrivanplays.ivanbin.handlers.FaviconRoute;
import com.mrivanplays.ivanbin.handlers.api.BinCreate;
import com.mrivanplays.ivanbin.handlers.api.BinInfo;
import com.mrivanplays.ivanbin.utils.Bin;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.hjson.JsonValue;
import spark.Route;

public class BinBootstrap {

  public static String notFoundHTML;
  public static String readerHTML;
  public static File binsDirectory;

  static {
    Path notFound = Paths.get("/usr/share/nginx/bin/not-found.html");
    Path readerFile = Paths.get("/usr/share/nginx/bin/reader.html");

    try (BufferedReader notFoundReader =
        Files.newBufferedReader(notFound, StandardCharsets.UTF_8)) {
      notFoundHTML = notFoundReader.lines().collect(Collectors.joining());
    } catch (IOException e) {
      e.printStackTrace();
    }

    try (BufferedReader readerFileReader =
        Files.newBufferedReader(readerFile, StandardCharsets.UTF_8)) {
      readerHTML = readerFileReader.lines().collect(Collectors.joining());
    } catch (IOException e) {
      e.printStackTrace();
    }

    binsDirectory = new File("/usr/share/nginx/bin/bins/");
    if (!binsDirectory.exists()) {
      binsDirectory.mkdirs();
    }
  }

  private static Config config;

  public static void main(String[] args) throws Exception {
    config = new Config();
    port(6869);
    initExceptionHandler(Throwable::printStackTrace);

    Route notFoundErr = htmlRenderingRoute(notFoundHTML);
    notFound(notFoundErr);

    Route favicon = new FaviconRoute();
    get("/favicon.ico", favicon);

    String defaultPageHTMLCache;

    Path defaultPagePath = Paths.get("/usr/share/nginx/bin/default-page.html");

    try (BufferedReader reader = Files.newBufferedReader(defaultPagePath, StandardCharsets.UTF_8)) {
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

  public static Optional<Bin> getBin(String id) throws IOException {
    File binFile = new File(binsDirectory, id + ".txt");
    if (binFile.exists()) {
      File jsonDataFile = new File(binsDirectory, id + ".json");
      try (BufferedReader reader =
          Files.newBufferedReader(jsonDataFile.toPath(), StandardCharsets.UTF_8)) {
        return Optional.of(new Bin(JsonValue.readJSON(reader).asObject(), binFile, jsonDataFile));
      }
    } else {
      return Optional.empty();
    }
  }

  public static String getBaseUrl() {
    return config.getBaseUrl();
  }

  private static Route htmlRenderingRoute(String html) {
    return (request, response) -> {
      response.type("text/html");
      response.status(200);
      return html.replace("{baseurl}", config.getBaseUrl());
    };
  }
}
