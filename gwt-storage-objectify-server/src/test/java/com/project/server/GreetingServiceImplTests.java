package com.project.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.project.shared.entities.GreetingResponse;

public class GreetingServiceImplTests extends Mockito {
  private static final String SERVER_INFO = "SERVER_INFO";
  private static final String USER_AGENT = "USER_AGENT";
  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  protected GreetingServiceImpl service;
  private Closeable closeable;

  @Before
  public void setUp() {
    helper.setUp();
    OfyHelper.register();
    closeable = ObjectifyService.begin();
    service = spy(GreetingServiceImpl.class);

    ServletContext servletContext = mock(ServletContext.class);
    doReturn(SERVER_INFO).when(servletContext).getServerInfo();
    doReturn(servletContext).when(service).getServletContext();

    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    doReturn(USER_AGENT).when(httpServletRequest).getHeader("User-Agent");
    doReturn(httpServletRequest).when(service).getThreadLocalRequest2();
  }

  @After
  public void tearDown() throws IOException {
    closeable.close();
    helper.tearDown();
  }

  @Test
  public void testGreetServer() {
    String username = "username";
    GreetingResponse greetingResponse1 = service.greetServer(username);
    ObjectifyService.ofy().flush();
    assertEquals(SERVER_INFO, greetingResponse1.getServerInfo());
    assertEquals(USER_AGENT, greetingResponse1.getUserAgent());
    assertTrue(greetingResponse1.getUserRef().isLoaded());
    assertEquals(username, greetingResponse1.getUserRef().get().getName());
    assertEquals(0, greetingResponse1.getCount());

    GreetingResponse greetingResponse2 = service.greetServer(username);
    ObjectifyService.ofy().flush();
    assertEquals(greetingResponse1.getUserRef(), greetingResponse2.getUserRef());
    assertEquals(1, greetingResponse2.getCount());
  }

  @Test
  public void testGreetServer_invalid() {
    expectedEx.expect(ConstraintViolationException.class);
    service.greetServer("@");
  }

  @Test
  public void testGreetServer_null() {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("user can't be null.");
    service.greetServer(null);
  }
}
