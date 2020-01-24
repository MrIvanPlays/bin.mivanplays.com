package com.mrivanplays.ivanbin.handlers.api;

import com.mrivanplays.ivanbin.BinBootstrap;
import com.mrivanplays.ivanbin.utils.Bin;
import java.util.Optional;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinDelete implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    response.type("application/json");
    String id = request.params(":id");
    Optional<Bin> binOptional = BinBootstrap.getBin(id);
    if (binOptional.isPresent()) {
      response.status(200);
      Bin bin = binOptional.get();
      bin.getDataFile().delete();
      bin.getFile().delete();
      return "{\"success\": true}";
    } else {
      response.status(404);

      return "{\"success\": false, \"error:\": \"404 not found\"}";
    }
  }
}
