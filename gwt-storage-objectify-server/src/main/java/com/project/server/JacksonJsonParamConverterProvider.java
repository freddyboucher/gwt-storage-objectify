package com.project.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
public class JacksonJsonParamConverterProvider implements ParamConverterProvider {

  private final Logger logger = Logger.getLogger(JacksonJsonParamConverterProvider.class.getName());

  @Context
  Providers providers;

  private static <T> boolean hasMethod(@Nonnull Class<T> rawType, @Nonnull String name) {
    try {
      return rawType.getMethod(name, String.class) != null;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }

  @Override
  public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
    if (String.class.equals(rawType) || hasMethod(rawType, "valueOf") || hasMethod(rawType, "fromString")) {
      return null;
    }

    // Check whether we can convert the given type with Jackson.
    MessageBodyReader<T> mbr = providers.getMessageBodyReader(rawType, genericType, annotations, MediaType.APPLICATION_JSON_TYPE);
    if (mbr == null || !mbr.isReadable(rawType, genericType, annotations, MediaType.APPLICATION_JSON_TYPE)) {
      return null;
    }

    // Obtain custom ObjectMapper for special handling.
    ContextResolver<ObjectMapper> contextResolver = providers.getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);

    ObjectMapper mapper = contextResolver != null ? contextResolver.getContext(rawType) : new ObjectMapper();

    logger.info("Create ParamConverter for rawType=" + rawType.getTypeName());
    return new ParamConverter<T>() {

      @Override
      public T fromString(String value) {
        try {
          return mapper.readerFor(rawType).readValue(value);
        } catch (Exception e) {
          throw new ProcessingException("rawType=" + rawType.getTypeName() + " value=" + value, e);
        }
      }

      @Override
      public String toString(T value) {
        try {
          return mapper.writer().writeValueAsString(value);
        } catch (JsonProcessingException e) {
          throw new ProcessingException("rawType=" + rawType.getTypeName(), e);
        }
      }
    };
  }
}