package com.mrivanplays.ivanbin.handlers.api;

import com.mrivanplays.ivanbin.BinBootstrap;
import com.mrivanplays.ivanbin.handlers.api.auth.AuthKeysFile;
import com.mrivanplays.ivanbin.utils.StringUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.hjson.JsonObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinCreate implements Route {

  private File binsDirectory;
  private AuthKeysFile authKeysFile;

  public BinCreate(AuthKeysFile authKeysFile) {
    this.binsDirectory = BinBootstrap.binsDirectory;
    this.authKeysFile = authKeysFile;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    response.type("application/json");
    String code = request.body();
    if (code == null || code.isEmpty()) {
      response.status(403);
      return "{\"error\": \"403 forbidden (empty/null string for code)\"";
    }
    String existing = checkForExisting(code);
    if (existing != null) {
      response.status(200);
      return "{\"binId\": \"" + existing + "\"}";
    }
    String binString = StringUtils.generateRandomString(11);
    File file = new File(binsDirectory, binString + ".txt");
    if (file.exists()) {
      binString = StringUtils.generateRandomString(11);
      file = new File(binsDirectory, binString + ".txt");
    }
    file.createNewFile();
    try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
      writer.write(code);
    }
    String createdAt = OffsetDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
    String expiresAt =
        OffsetDateTime.now().plusHours(72).format(DateTimeFormatter.RFC_1123_DATE_TIME);
    JsonObject dataObject = new JsonObject();
    dataObject.add("binId", binString);
    dataObject.add("createdAt", createdAt);
    dataObject.add("expiresAt", expiresAt);
    dataObject.add("body", code);
    String ip = request.ip();
    String specifiedAuthKey = request.headers("Auth-Key");
    if (specifiedAuthKey != null) {
      String ipAuthKey = authKeysFile.getAuthKey(ip);
      if (!ipAuthKey.equalsIgnoreCase(specifiedAuthKey)) {
        dataObject.add("owner", "none");
      } else {
        dataObject.add("owner", ipAuthKey);
      }
    } else {
      dataObject.add("owner", "none");
    }

    File jsonData = new File(binsDirectory, binString + ".json");
    jsonData.createNewFile();
    try (Writer writer = Files.newBufferedWriter(jsonData.toPath(), StandardCharsets.UTF_8)) {
      writer.write(dataObject.toString());
    }
    response.status(200);
    return "{\"binId\": \"" + binString + "\"}";
  }

  private String checkForExisting(String code) {
    File[] files = BinBootstrap.binsDirectory.listFiles(($, name) -> name.endsWith(".txt"));
    if (files == null || files.length == 0) {
      return null;
    }
    for (File file : files) {
      try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
        String readCode = reader.lines().collect(Collectors.joining("\n"));
        if (StringUtils.equals(code, readCode)) {
          return file.getName().replace(".txt", "");
        }
      } catch (IOException ignored) {
      }
    }
    return null;
  }
}
