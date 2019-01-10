package com.project.server;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.ws.rs.core.Context;

import com.google.common.collect.ImmutableSet;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.project.shared.GreetingService;
import com.project.shared.entities.GreetingResponse;
import com.project.shared.entities.User;

public class GreetingServiceImpl implements GreetingService {

  private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  @Context protected HttpServletRequest request;
  @Context protected ServletContext context;

  @Override
  public void clearUsers() {
    ObjectifyService.ofy().delete().keys(ObjectifyService.ofy().load().type(User.class).keys());
    ObjectifyService.ofy().delete().keys(ObjectifyService.ofy().load().type(GreetingResponse.class).keys());
  }

  @Override
  public List<User> getUsers() {
    return ObjectifyService.ofy().load().type(User.class).list();
  }

  @Override
  public GreetingResponse greetServer(String username, @Nonnull List<Integer> unusedList, @Nonnull Map<String, Integer> unusedMaps) {
    Objects.requireNonNull(unusedList);
    Objects.requireNonNull(unusedMaps);
    if (username == null) {
      throw new IllegalArgumentException("user can't be null.");
    }
    Set<ConstraintViolation<User>> violations = validator.validateValue(User.class, "name", username, Default.class);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(ImmutableSet.copyOf(violations));
    }

    User user = ObjectifyService.ofy().load().type(User.class).filter("name", username).first().now();
    if (user == null) {
      user = new User();
      user.setName(username);
      ObjectifyService.ofy().save().entity(user).now();
    }

    int count = ObjectifyService.ofy().load().type(GreetingResponse.class).filter("userRef", user).count();

    GreetingResponse response = new GreetingResponse();
    response.setServerInfo(context.getServerInfo());
    response.setUserAgent(request.getHeader("User-Agent"));
    response.setUserRef(Ref.create(user));
    ObjectifyService.ofy().save().entity(response).now();

    response.setCount(count);
    return response;
  }
}
