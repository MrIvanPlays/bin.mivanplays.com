package com.mrivanplays.ivanbin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

public class Config {

  private JsonObject object;

  public Config() throws IOException {
    File workdir = new File("");
    File configFile = new File(workdir, "config.hjson");
    if (!configFile.exists()) {
      try {
        configFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.hjson")) {
        Files.copy(in, configFile.toPath());
      }
    }
    try (Reader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8)) {
      object = JsonValue.readHjson(reader).asObject();
    }
  }

  public String getBaseUrl() {
    return object.getString("baseURL", "https://bin.mrivanplays.com/");
  }
}
