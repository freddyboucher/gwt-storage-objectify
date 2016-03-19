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
import com.project.shared.GreetingService;
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
    return Lists.newArrayList(ObjectifyService.ofy().load().type(User.class).list());
  }

  @Override
  public User save(User user) {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<User>> violations = validator.validate(user, Default.class);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(ImmutableSet.<ConstraintViolation<?>> copyOf(violations));
    }
    ObjectifyService.ofy().save().entity(user).now();
    return user;
  }

}
