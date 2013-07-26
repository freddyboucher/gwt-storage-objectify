package com.project.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.project.client.GreetingService;
import com.project.shared.entities.User;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

  static {
    ObjectifyService.register(User.class);
  }

  @Override
  public void clearUsers() {
    ObjectifyService.ofy().delete().keys(ObjectifyService.ofy().load().type(User.class).keys());
  }

  @Override
  public List<User> getUsers() {
    return new ArrayList<User>(ObjectifyService.ofy().load().type(User.class).list());
  }

  @Override
  public User save(User user) {
    ObjectifyService.ofy().save().entity(user).now();
    return user;
  }

}
