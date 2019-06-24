package com.project.server;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import com.project.shared.RestConstant;

@Component
@ApplicationPath(RestConstant.BASE_URI)
public class JaxRsApplication extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    return ImmutableSet
        .of(GreetingServiceImpl.class, DeobfuscatorServiceImpl.class, JacksonConfig.class, JaxRsExceptionMapper.class, JacksonJsonParamConverterProvider.class,
            JsonStringProvider.class);
  }
}
