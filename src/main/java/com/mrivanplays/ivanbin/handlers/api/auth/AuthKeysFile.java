package com.mrivanplays.ivanbin.handlers.api.auth;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

public class AuthKeysFile {

  private File file;
  private JsonArray keys;

  public AuthKeysFile() {
    // todo: see BinBootstrap:42
    this.file = new File("/usr/share/nginx/bin/authkeys.json");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
      String bean = reader.lines().collect(Collectors.joining());
      if (bean.isEmpty()) {
        keys = new JsonArray();
        return;
      }
      keys = JsonValue.readJSON(bean).asArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getAuthKey(String ip) {
    for (JsonValue key : keys) {
      JsonObject keyObject = key.asObject();
      if (keyObject.getString("generatorIp", null).equalsIgnoreCase(ip)) {
        return keyObject.getString("authKey", null);
      }
    }
    return null;
  }

  public void setAuthKey(String ip, String key) {
    JsonObject object = new JsonObject();
    object.add("generatorIp", ip);
    object.add("authKey", key);
    keys.add(object);
    try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
      writer.write(keys.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
