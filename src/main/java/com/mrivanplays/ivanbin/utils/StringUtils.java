package com.mrivanplays.ivanbin.utils;

import java.util.concurrent.ThreadLocalRandom;

public class StringUtils {

  private static final String data =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  public static String generateRandomString(int length) {
    if (length < 5) {
      throw new IllegalArgumentException("Length should be at least 5 characters.");
    }

    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomCharAt = ThreadLocalRandom.current().nextInt(0, data.length());
      char randomChar = data.charAt(randomCharAt);

      builder.append(randomChar);
    }

    return builder.toString();
  }

  public static boolean equals(String one, String two) {
    return applyReplacements(one).equalsIgnoreCase(applyReplacements(two));
  }

  private static String applyReplacements(String s) {
    return s.replace(" ", "").replace("\n", "");
  }
}
