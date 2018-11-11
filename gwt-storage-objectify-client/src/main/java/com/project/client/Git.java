package com.project.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Git {
  @JsonProperty("git.commit.id.abbrev") String gitCommitIdAbbrev;
  @JsonProperty("git.dirty") Boolean gitDirty;
}
