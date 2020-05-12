package com.project.client;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class Car {
  @JsProperty(namespace = JsPackage.GLOBAL) public static Car car;

  public native String start();
}
