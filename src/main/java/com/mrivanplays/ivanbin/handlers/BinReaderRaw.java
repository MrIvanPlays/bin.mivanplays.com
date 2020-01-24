package com.mrivanplays.ivanbin.handlers;

import com.mrivanplays.ivanbin.BinBootstrap;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinReaderRaw implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String id = request.params(":id");
    File file = new File(BinBootstrap.binsDirectory, id + ".txt");
    if (!file.exists()) {
      response.type("text/html");
      response.status(404);

      return BinBootstrap.notFoundHTML;
    }
    try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
      response.type("text/html");
      response.status(200);

      return toHTML(
          reader
              .lines()
              .collect(Collectors.joining("\n"))
              .replace("<", "&lt;")
              .replace(">", "&gt;"));
    }
  }

  private String toHTML(String code) {
    return "<html><head><meta charset=\"utf-8\"/></head><body><pre><code>"
        + code
        + "</code></pre></body></html>";
  }
}
