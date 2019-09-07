package com.project.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.project.shared.entities.User;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = SpringBootApplication.class)
@ContextConfiguration(classes = SrpingBootApplicationMockIntegrationTest.Config.class)
@AutoConfigureMockMvc
public class SrpingBootApplicationMockIntegrationTest {
  private LocalServiceTestHelper helper;
  @Autowired private MockMvc mvc;

  @Before
  public void setUp() {
    helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    helper.setUp();
    OfyHelper.register();
  }

  @After
  public void tearDown() throws IOException {
    helper.tearDown();
  }

  @Test
  public void testIndex() throws Exception {
    // You cannot write assertions for the content of a JSP page because JSP pages are rendered by a servlet container and Spring MVC Test doesn't run a
    // servlet container. You can only verify that the view name is correct and/or the request is forwarded to the correct url.
    mvc.perform(get("/").contentType(MediaType.TEXT_HTML)).andExpect(status().isOk()).andExpect(forwardedUrl("/index.jsp")).andExpect(content().string(""));
  }

  @Test
  public void testGetUsers() throws Exception {
    // SpringBootTest.WebEnvironment.MOCK does not seem to mock Resteasy mappings
    mvc.perform(get("/api/greeting/getUsers").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
  }

  @Test
  public void testApiUserCreate() throws Exception {
    // SpringBootTest.WebEnvironment.MOCK does mock Spring mappings
    mvc.perform(post("/api/user/create").param("username", "Freddy")).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andExpect(content().json("{\"id\":1,\"name\":\"Freddy\"}"));
  }

  @RestController
  public static class Config {

    @RequestMapping(value = "/api/user/create", method = RequestMethod.POST)
    public User apiUserCreate(@RequestParam("username") String username) {
      User user = new User();
      user.setName(username);
      ObjectifyService.ofy().save().entity(user).now();
      return user;
    }
  }
}
