package com.project.shared;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fusesource.restygwt.client.DirectRestService;

@Path("remote-logging")
public interface RemoteLoggingService extends DirectRestService {

  @POST
  @Path("logOnServer")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  String logOnServer(@FormParam("throwable") Throwable throwable, @FormParam("level") String level,
      @FormParam("permutationStrongName") String permutationStrongName);
}
