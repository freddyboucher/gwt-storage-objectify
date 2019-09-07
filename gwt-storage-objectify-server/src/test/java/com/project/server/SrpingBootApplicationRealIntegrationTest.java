package com.project.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RestController;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.apphosting.api.ApiProxy;
import com.project.shared.GreetingService;
import com.project.shared.RestConstant;
import com.project.shared.entities.GreetingResponse;

import io.undertow.servlet.api.ThreadSetupHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = SpringBootApplication.class)
@TestPropertySource(properties = "server.port=8080")
@ContextConfiguration(classes = SrpingBootApplicationRealIntegrationTest.Config.class)
public class SrpingBootApplicationRealIntegrationTest {
  private static ApiProxy.Environment currentEnvironment;
  private ResteasyClient client;
  private LocalServiceTestHelper helper;
  private GreetingService greetingService;

  @Before
  public void setUp() {
    helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    helper.setUp();
    currentEnvironment = ApiProxy.getCurrentEnvironment();

    client = (ResteasyClient) ClientBuilder.newBuilder().register(new CustomResteasyJackson2Provider()).build();
    ResteasyWebTarget target = client.target("http://127.0.0.1:8080/").path(RestConstant.BASE_URI);
    greetingService = target.proxy(GreetingService.class);
  }

  @After
  public void tearDown() throws IOException {
    helper.tearDown();
  }

  @Test
  public void testIndex() throws Exception {
    ClientResponse invoke = (ClientResponse) client.target("http://127.0.0.1:8080/").request(MediaType.TEXT_HTML).buildGet().invoke();
    String html = invoke.readEntity(String.class);
    assertTrue(html.contains("<title>Spring Boot / GWT / JAX-RS demo project</title>"));
  }

  @Test
  public void testGreetServer() throws Exception {
    GreetingResponse response = greetingService.greetServer("Freddy", Collections.emptyList(), Collections.emptyMap());
    assertEquals("Undertow - 2.0.23.Final", response.getServerInfo());
    assertTrue(response.getUserAgent().startsWith("Apache-HttpClient/4.5.4 (Java/1.8.0_"));
    assertEquals(0, response.getCount());
    assertEquals(2L, response.getId().longValue());
    assertEquals(1L, response.getUserRef().get().getId().longValue());
    assertEquals("Freddy", response.getUserRef().get().getName());
  }

  @Test
  public void testGetUsers() throws Exception {
    assertTrue(greetingService.getUsers().isEmpty());
  }

  static class CustomResteasyJackson2Provider extends ResteasyJackson2Provider {
    public CustomResteasyJackson2Provider() {
      setMapper(new JacksonConfig().getContext(null));
    }
  }

  @Configuration
  @RestController
  public static class Config {
    @Bean
    ServletWebServerFactory servletWebServerFactory() {
      UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory(8080);
      factory.addDeploymentInfoCustomizers(deploymentInfo -> deploymentInfo.addThreadSetupAction(new ThreadSetupHandler() {
        @Override
        public <T, C> Action<T, C> create(Action<T, C> action) {
          return (exchange, context) -> {
            ApiProxy.setEnvironmentForCurrentThread(currentEnvironment);
            return action.call(exchange, context);
          };
        }
      }));
      return factory;
    }
  }
}
