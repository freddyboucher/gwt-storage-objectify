package com.project.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.github.nmorel.gwtjackson.remotelogging.shared.RemoteThrowable;
import com.google.common.base.Throwables;
import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.project.shared.DeobfuscatorService;

public class DeobfuscatorServiceImpl implements DeobfuscatorService {
  private final Logger logger = Logger.getLogger(getClass().getName());
  private final StackTraceDeobfuscator deobfuscator = StackTraceDeobfuscator.fromFileSystem("WEB-INF/deploy/app/symbolMaps/");

  @Override
  public String deobfuscate(@Nonnull RemoteThrowable throwable, @Nonnull String level, @Nonnull String permutationStrongName) {
    deobfuscator.deobfuscateStackTrace(throwable, permutationStrongName);
    logger.log(Level.parse(level), "Deobfuscate client Throwable.", throwable);
    return Throwables.getStackTraceAsString(throwable);
  }
}
