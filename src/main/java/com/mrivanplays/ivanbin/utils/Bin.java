package com.mrivanplays.ivanbin.utils;

import java.io.File;
import org.hjson.JsonObject;

public class Bin {

  private JsonObject data;
  private File file;
  private File dataFile;

  public Bin(JsonObject data, File file, File dataFile) {
    this.data = data;
    this.file = file;
    this.dataFile = dataFile;
  }

  public JsonObject getData() {
    return data;
  }

  public File getFile() {
    return file;
  }

  public File getDataFile() {
    return dataFile;
  }
}
