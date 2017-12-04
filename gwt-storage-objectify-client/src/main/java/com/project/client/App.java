package com.project.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.UmbrellaException;
import com.project.shared.RemoteLoggingService;
import com.project.shared.RestConstant;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {

  public static final RemoteLoggingService REMOTE_LOGGING_SERVICE = GWT.create(RemoteLoggingService.class);

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad() {
    GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
      @Override
      public void onUncaughtException(Throwable e) {
        Throwable throwable = unwrap(e);
        REST.withCallback(new MethodCallback<String>() {
          @Override
          public void onFailure(Method method, Throwable throwable) {
            Logger.getLogger("AppEntryPoint").log(Level.SEVERE, "Cannot log on Server.", throwable);
          }

          @Override
          public void onSuccess(Method method, String response) {
            Logger.getLogger("AppEntryPoint").info(response);
          }
        }).call(REMOTE_LOGGING_SERVICE).logOnServer(throwable, "SEVERE", GWT.getPermutationStrongName());
        Logger.getLogger("AppEntryPoint").log(Level.SEVERE, "", throwable);
      }

      @Nonnull
      public Throwable unwrap(@Nonnull Throwable e) {
        if (e instanceof UmbrellaException) {
          UmbrellaException ue = (UmbrellaException) e;
          if (ue.getCauses().size() == 1) {
            return unwrap(ue.getCauses().iterator().next());
          }
        }
        return e;
      }
    });
    Scheduler.get().scheduleDeferred(() -> {
      Defaults.setServiceRoot(RestConstant.BASE_URI);
      Defaults.setDateFormat(null);
      Defaults.setDispatcher((method, builder) -> builder.send());
      RootPanel.get().add(new View());
    });
  }
}
