package com.mrivanplays.ivanbin.utils;

import java.io.File;
import org.json.JSONObject;

public class Bin {

  private JSONObject data;
  private File file;
  private File dataFile;

  public Bin(JSONObject data, File file, File dataFile) {
    this.data = data;
    this.file = file;
    this.dataFile = dataFile;
  }

  public JSONObject getData() {
    return data;
  }

  public File getFile() {
    return file;
  }

  public File getDataFile() {
    return dataFile;
  }
}
