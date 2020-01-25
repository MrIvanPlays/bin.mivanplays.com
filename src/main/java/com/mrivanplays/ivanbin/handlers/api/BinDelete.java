package com.mrivanplays.ivanbin.handlers.api;

import com.mrivanplays.ivanbin.BinBootstrap;
import com.mrivanplays.ivanbin.handlers.api.auth.AuthKeysFile;
import com.mrivanplays.ivanbin.utils.Bin;
import java.util.Optional;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinDelete implements Route {

  private AuthKeysFile authKeysFile;

  public BinDelete(AuthKeysFile authKeysFile) {
    this.authKeysFile = authKeysFile;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    response.type("application/json");
    String ip = request.ip();
    String authKeySpecified = request.headers("Auth-Key");
    if (authKeySpecified != null) {
      String ipAuthKey = authKeysFile.getAuthKey(ip);
      if (!authKeySpecified.equalsIgnoreCase(ipAuthKey)) {
        response.status(403);
        return "{\"success\": false, \"error\": \"403 forbidden (invalid auth key)\"}";
      } else {
        if (authKeySpecified.equalsIgnoreCase("none")) {
          return "{\"success\": false, \"error\": \"403 forbidden (invalid auth key)\"}";
        }
        String id = request.params(":id");
        Optional<Bin> binOptional = BinBootstrap.getBin(id);
        if (binOptional.isPresent()) {
          Bin bin = binOptional.get();
          if (!bin.getData().getString("owner", null).equalsIgnoreCase(ipAuthKey)) {
            response.status(403);
            return "{\"success\": false, \"error\": \"403 forbidden (not owner of the bin)\"";
          }
          response.status(200);
          bin.getDataFile().delete();
          bin.getFile().delete();
          return "{\"success\": true}";
        } else {
          response.status(404);

          return "{\"success\": false, \"error\": \"404 not found\"}";
        }
      }
    } else {
      response.status(403);

      return "{\"success\": false, \"error\": \"403 forbidden (no auth key specified)\"}";
    }
  }
}
