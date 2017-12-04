package com.project.server;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nmorel.gwtjackson.objectify.server.ObjectifyJacksonModule;
import com.github.nmorel.gwtjackson.remotelogging.server.RemoteLoggingJacksonModule;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonConfig implements ContextResolver<ObjectMapper> {
  private final ObjectMapper objectMapper;

  public JacksonConfig() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new ObjectifyJacksonModule());
    objectMapper.registerModule(new RemoteLoggingJacksonModule());
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return objectMapper;
  }
}
