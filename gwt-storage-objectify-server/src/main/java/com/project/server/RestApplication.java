package com.project.server;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.google.common.collect.ImmutableSet;
import com.project.shared.RestConstant;

@ApplicationPath(RestConstant.BASE_URI)
public class RestApplication extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    return ImmutableSet.of(GreetingServiceImpl.class, JacksonConfig.class, RestExceptionMapper.class);
  }
}
