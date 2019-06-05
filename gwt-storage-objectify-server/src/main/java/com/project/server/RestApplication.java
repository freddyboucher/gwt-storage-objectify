package com.project.server;

import java.util.Set;

import javax.ws.rs.core.Application;

import com.google.common.collect.ImmutableSet;

public class RestApplication extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    return ImmutableSet
        .of(GreetingServiceImpl.class, DeobfuscatorServiceImpl.class, JacksonConfig.class, RestExceptionMapper.class, JacksonJsonParamConverterProvider.class,
            JsonStringProvider.class);
  }
}
