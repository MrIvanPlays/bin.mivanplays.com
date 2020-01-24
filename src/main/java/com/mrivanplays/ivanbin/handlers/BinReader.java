package com.mrivanplays.ivanbin.handlers;

import com.mrivanplays.ivanbin.BinBootstrap;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinReader implements Route {

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

      Stream<String> lineStream = reader.lines();

      StringBuilder lineNumbers = new StringBuilder();

      for (int i = 0; i < lineStream.count(); i++) {
        lineNumbers.append((i + 1)).append("<br>");
      }

      String codeInline =
          lineStream.collect(Collectors.joining("\n")).replace("<", "&lt;").replace(">", "&gt;");

      return BinBootstrap.readerHTML
          .replace("{code_here}", codeInline)
          .replace("{lch}", lineNumbers);
    }
  }
}
