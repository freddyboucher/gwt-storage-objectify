package com.project.server;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.project.shared.GreetingService;
import com.project.shared.entities.GreetingResponse;
import com.project.shared.entities.User;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

  static {
    ObjectifyService.register(User.class);
    ObjectifyService.register(GreetingResponse.class);
  }

  @Override
  public void clearUsers() {
    ObjectifyService.ofy().delete().keys(ObjectifyService.ofy().load().type(User.class).keys());
    ObjectifyService.ofy().delete().keys(ObjectifyService.ofy().load().type(GreetingResponse.class).keys());
  }

  @Override
  public List<User> getUsers() {
    return Lists.newArrayList(ObjectifyService.ofy().load().type(User.class).list());
  }

  @Override
  public GreetingResponse greetServer(String username) throws IllegalArgumentException {
    if (username == null) {
      throw new IllegalArgumentException("user can't be null.");
    }
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<User>> violations = validator.validateValue(User.class, "name", username, Default.class);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(ImmutableSet.<ConstraintViolation<?>> copyOf(violations));
    }

    User user = ObjectifyService.ofy().load().type(User.class).filter("name", username).first().now();
    if (user == null) {
      user = new User();
      user.setName(username);
      ObjectifyService.ofy().save().entity(user).now();
    }

    Ref<User> userRef = Ref.create(user);

    final int count = ObjectifyService.ofy().load().type(GreetingResponse.class).filter("userRef", userRef).count();

    GreetingResponse response = new GreetingResponse();
    response.setServerInfo(getServletContext().getServerInfo());
    response.setUserAgent(getThreadLocalRequest().getHeader("User-Agent"));
    response.setUserRef(userRef);
    ObjectifyService.ofy().save().entity(response);

    response.setCount(count);
    return response;
  }
}
