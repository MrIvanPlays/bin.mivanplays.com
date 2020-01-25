package com.mrivanplays.ivanbin.handlers.api.auth;

import com.mrivanplays.ivanbin.utils.StringUtils;
import spark.Request;
import spark.Response;
import spark.Route;

public class AuthKeyGenerator implements Route {

  private AuthKeysFile authKeysFile;

  public AuthKeyGenerator(AuthKeysFile authKeysFile) {
    this.authKeysFile = authKeysFile;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String ip = request.ip();
    response.status(200);
    response.type("application/json");
    if (authKeysFile.getAuthKey(ip) == null) {
      String key = StringUtils.generateRandomString(36);
      authKeysFile.setAuthKey(ip, key);
      return "{\"authKey\": \"" + key + "\"}";
    } else {
      String key = authKeysFile.getAuthKey(ip);
      return "{\"authKey\": \"" + key + "\"}";
    }
  }
}
