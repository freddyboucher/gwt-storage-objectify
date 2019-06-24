package com.project.server;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.googlecode.objectify.ObjectifyFilter;

@RestController
@org.springframework.boot.autoconfigure.SpringBootApplication
public class SpringBootApplication extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SpringBootApplication.class);
  }

  @GetMapping("/")
  public ModelAndView welcome() {
    return new ModelAndView("/index.jsp");
  }

  @Bean
  public FilterRegistrationBean<ObjectifyFilter> objectifyFilter() {
    FilterRegistrationBean bean = new FilterRegistrationBean(new ObjectifyFilter());
    bean.addUrlPatterns("/api/*");
    return bean;
  }
}
