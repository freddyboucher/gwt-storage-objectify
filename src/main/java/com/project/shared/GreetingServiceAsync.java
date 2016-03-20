package com.project.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.shared.entities.GreetingResponse;
import com.project.shared.entities.User;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
  void clearUsers(AsyncCallback<Void> callback);

  void getUsers(AsyncCallback<List<User>> callback);

  void greetServer(String username, AsyncCallback<GreetingResponse> callback);
}
