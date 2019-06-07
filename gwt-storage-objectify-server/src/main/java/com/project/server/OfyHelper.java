package com.project.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;

import com.googlecode.objectify.ObjectifyService;
import com.project.shared.entities.GreetingResponse;
import com.project.shared.entities.User;

@WebListener
public class OfyHelper implements ServletContextListener {
  public static void register() {
    ObjectifyService.register(User.class);
    ObjectifyService.register(GreetingResponse.class);
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    // App Engine does not currently invoke this method.
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    assert event.getServletContext().getServletRegistration(HttpServlet30Dispatcher.class.getName()) == null;
    // This will be invoked as part of a warmup request, or the first user request if no warmup request was invoked.
    register();
  }
}
