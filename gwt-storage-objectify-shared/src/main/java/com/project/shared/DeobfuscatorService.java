package com.project.shared;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fusesource.restygwt.client.DirectRestService;

@Path("deobfuscator")
public interface DeobfuscatorService extends DirectRestService {

  @POST
  @Path("deobfuscate")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  String deobfuscate(@FormParam("throwable") Throwable throwable, @FormParam("level") String level,
      @FormParam("permutationStrongName") String permutationStrongName);
}
