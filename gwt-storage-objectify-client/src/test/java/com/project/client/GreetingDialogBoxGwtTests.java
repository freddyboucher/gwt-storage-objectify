package com.project.client;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.project.shared.entities.GreetingResponse;

public class GreetingDialogBoxGwtTests extends GWTTestCase {
  private static final String GREETING_RESPONSE =
      "{\"id\":5724183724032000,\"serverInfo\":\"Google App Engine/Google App Engine/1.9.79\",\"userAgent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36\",\"userRef\":{\"key\":{\"raw\":{\"kind\":\"User\",\"id\":5767253387640832}},\"value\":{\"id\":5767253387640832,\"name\":\"Freddy\"}},\"count\":0}";

  @Override
  public String getModuleName() {
    return "com.project.AppJUnit";
  }

  public void testView() {
    GreetingDialogBox view = new GreetingDialogBox(GreetingResponseMapper.INSTANCE.read(GREETING_RESPONSE));
    assertNotNull(view);
    assertEquals(
        "<div class=\"gwt-HTML\">Hello, Freddy!<br>This is your greeting message nº1.<br><br>I am running Google App Engine/Google App Engine/1.9.79.<br><br>It looks like you are using:<br>Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36</div>",
        view.serverResponseLabel.toString());
  }

  public interface GreetingResponseMapper extends ObjectMapper<GreetingResponse> {
    GreetingResponseMapper INSTANCE = GWT.create(GreetingResponseMapper.class);
  }
}
