package com.mrivanplays.ivanbin;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

public class Config {

  private JsonObject object;

  public Config() throws IOException {
    Path configFile = Paths.get(".", "config.hjson");
    if (!Files.exists(configFile)) {
      Path actualFilePath = Files.createFile(configFile);
      try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.hjson")) {
        Files.copy(in, actualFilePath);
      }
    }
    try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
      object = JsonValue.readHjson(reader).asObject();
    }
  }

  public String getBaseUrl() {
    return object.getString("baseURL", "https://bin.mrivanplays.com/");
  }
}
