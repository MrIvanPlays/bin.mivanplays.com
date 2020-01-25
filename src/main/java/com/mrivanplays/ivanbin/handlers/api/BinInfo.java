package com.mrivanplays.ivanbin.handlers.api;

import com.mrivanplays.ivanbin.BinBootstrap;
import com.mrivanplays.ivanbin.utils.Bin;
import java.util.Optional;
import org.hjson.JsonObject;
import spark.Request;
import spark.Response;
import spark.Route;

public class BinInfo implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    response.type("application/json");
    String id = request.params(":id");
    Optional<Bin> binOptional = BinBootstrap.getBin(id);
    if (binOptional.isPresent()) {
      response.status(200);

      // the data now contains owner, which is being stored like
      // auth key which means this shouldn't be displayed in the data.
      // creating a object copy isn't the ideal solution here, as the ram usage will rise.
      // TODO: make this more ram efficient
      JsonObject data = binOptional.get().getData();
      JsonObject copy = new JsonObject(data);
      copy.remove("owner");
      return copy.toString();
    } else {
      response.status(404);

      return "{\"error\": \"404 not found\"}";
    }
  }
}
