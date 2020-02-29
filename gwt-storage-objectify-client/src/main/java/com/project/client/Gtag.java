package com.project.client;

import javax.annotation.Nonnull;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public abstract class Gtag {
  @JsMethod(namespace = JsPackage.GLOBAL)
  public static native void gtag(@Nonnull String event, @Nonnull String name, @Nonnull ExceptionParameters parameters);

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
  public static class ExceptionParameters {
    public String description;
    public Boolean fatal;

    @JsOverlay
    public static ExceptionParameters create(String description, Boolean fatal) {
      ExceptionParameters exceptionParameters = new ExceptionParameters();
      exceptionParameters.description = description;
      exceptionParameters.fatal = fatal;
      return exceptionParameters;
    }
  }
}
