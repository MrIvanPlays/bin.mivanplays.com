package com.mrivanplays.ivanbin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class BinChecker implements Runnable {

  @Override
  public void run() {
    File[] files = BinBootstrap.binsDirectory.listFiles(($, name) -> name.endsWith(".json"));
    if (files == null) {
      return;
    }
    List<SimpleBin> toDelete = new ArrayList<>();
    for (File jsonFiles : files) {
      try (BufferedReader reader =
          Files.newBufferedReader(jsonFiles.toPath(), StandardCharsets.UTF_8)) {
        JSONObject object = new JSONObject(reader.lines().collect(Collectors.joining()));
        TemporalAccessor createdAt =
            DateTimeFormatter.RFC_1123_DATE_TIME.parse(object.getString("createdAt"));
        Duration duration = Duration.between(OffsetDateTime.from(createdAt), OffsetDateTime.now());
        if (duration.toHours() >= 72) {
          toDelete.add(new SimpleBin(jsonFiles, object.getString("binId")));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!toDelete.isEmpty()) {
      for (SimpleBin bin : toDelete) {
        File file = new File(BinBootstrap.binsDirectory, bin.binId + ".txt");
        file.delete();
        bin.jsonFile.delete();
      }
      toDelete.clear();
    }
    // why calling a system gc?
    // the runnable have made lots of things that are going to be held into the system for a long
    // time, until the jvm realize that it doesn't need them. This way the program is freeing up
    // some
    // memory.
    System.gc();
  }

  private static class SimpleBin {

    File jsonFile;
    String binId;

    SimpleBin(File jsonFile, String binId) {
      this.jsonFile = jsonFile;
      this.binId = binId;
    }
  }
}
