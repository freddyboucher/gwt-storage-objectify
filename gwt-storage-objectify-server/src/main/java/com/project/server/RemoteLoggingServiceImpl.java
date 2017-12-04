package com.project.server;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.logging.server.RemoteLoggingServiceUtil;
import com.project.shared.RemoteLoggingService;

public class RemoteLoggingServiceImpl implements RemoteLoggingService {
  private final Logger logger = Logger.getLogger(getClass().getName());
  private final StackTraceDeobfuscator deobfuscator = StackTraceDeobfuscator.fromFileSystem("WEB-INF/deploy/app/symbolMaps/");

  @Context
  Providers providers;

  @Override
  public String logOnServer(@Nonnull Throwable throwable, @Nonnull String level, @Nonnull String permutationStrongName) {
    LogRecord logRecord = new LogRecord(Level.parse(level), "Client Exception.");
    logRecord.setThrown(throwable);
    logger.log(RemoteLoggingServiceUtil.deobfuscateLogRecord(deobfuscator, logRecord, permutationStrongName));
    try {
      ObjectMapper objectMapper = providers.getContextResolver(ObjectMapper.class, MediaType.WILDCARD_TYPE).getContext(null);
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(throwable);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
