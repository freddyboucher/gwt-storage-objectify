package com.project.client;

import org.fusesource.restygwt.client.Defaults;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.shared.RestConstant;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad() {
    Defaults.setServiceRoot(RestConstant.BASE_URI);
    RootPanel.get().add(new View());
  }
}
