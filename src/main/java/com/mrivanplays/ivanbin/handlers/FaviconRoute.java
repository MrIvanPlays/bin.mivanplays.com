package com.mrivanplays.ivanbin.handlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import spark.Request;
import spark.Response;
import spark.Route;

public class FaviconRoute implements Route {

  private File faviconFile;

  public FaviconRoute() {
    faviconFile = new File("/usr/share/nginx/favicon.ico");
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    response.raw().setContentType("image/vnd.microsoft.icon");
    response.status(200);
    try (InputStream in = new BufferedInputStream(new FileInputStream(faviconFile))) {
      try (OutputStream out = new BufferedOutputStream(response.raw().getOutputStream())) {
        byte[] buf = new byte[8192];
        while (true) {
          int r = in.read(buf);
          if (r == -1) {
            break;
          }
          out.write(buf, 0, r);
        }
      }
    }
    return "";
  }
}
