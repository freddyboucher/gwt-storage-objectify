package com.project.shared.entities;

import java.io.Serializable;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

@Entity
public class GreetingResponse implements Serializable {
  @Id
  private Long id;
  private String serverInfo;
  private String userAgent;
  @Index
  private Ref<User> userRef;
  @Ignore
  private int count;

  public int getCount() {
    return count;
  }

  public Long getId() {
    return id;
  }

  public String getServerInfo() {
    return serverInfo;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public Ref<User> getUserRef() {
    return userRef;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setServerInfo(String serverInfo) {
    this.serverInfo = serverInfo;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public void setUserRef(Ref<User> userRef) {
    this.userRef = userRef;
  }

}