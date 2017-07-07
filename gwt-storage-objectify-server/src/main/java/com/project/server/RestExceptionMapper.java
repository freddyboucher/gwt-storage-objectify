package com.project.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {
  private final Logger logger = Logger.getLogger(RestExceptionMapper.class.getName());
  @Context
  HttpHeaders headers;
  @Context
  Providers providers;

  @Override
  public Response toResponse(Throwable throwable) {
    logger.log(Level.SEVERE, "", throwable);
    Response.ResponseBuilder builder = Response.serverError();

    try {
      ObjectMapper objectMapper = providers.getContextResolver(ObjectMapper.class, MediaType.WILDCARD_TYPE).getContext(null);
      builder.entity(objectMapper.writeValueAsString(throwable));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    List<MediaType> accepts = headers.getAcceptableMediaTypes();
    if (accepts != null && !accepts.isEmpty()) {
      MediaType m = accepts.get(0);
      builder = builder.type(m);
    } else {
      builder = builder.type(headers.getMediaType());
    }
    return builder.build();
  }
}
