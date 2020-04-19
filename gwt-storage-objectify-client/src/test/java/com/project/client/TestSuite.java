package com.project.client;

import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

public class TestSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite();
    suite.addTestSuite(GreetingDialogBoxGwtTests.class);
    return suite;
  }
}
