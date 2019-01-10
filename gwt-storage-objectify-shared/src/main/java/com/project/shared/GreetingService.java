package com.project.shared;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.fusesource.restygwt.client.DirectRestService;

import com.project.shared.entities.GreetingResponse;
import com.project.shared.entities.User;

@Path("greeting")
public interface GreetingService extends DirectRestService {

  @GET
  @Path("clearUsers")
  @Produces(MediaType.APPLICATION_JSON)
  void clearUsers();

  @GET
  @Path("getUsers")
  @Produces(MediaType.APPLICATION_JSON)
  List<User> getUsers();

  @GET
  @Path("greetServer")
  @Produces(MediaType.APPLICATION_JSON)
  GreetingResponse greetServer(@QueryParam("username") String username, @Nonnull @QueryParam("unusedList") List<Integer> unusedList,
      @Nonnull @QueryParam("unusedMaps") Map<String, Integer> unusedMaps);
}
