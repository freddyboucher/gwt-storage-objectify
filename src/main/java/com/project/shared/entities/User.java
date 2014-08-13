package com.project.shared.entities;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Index
public class User implements Serializable {
  @Id
  private Long id;
  private String name;
  private Key<User> userKey;

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Key<User> getUserKey() {
    return userKey;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUserKey(Key<User> userKey) {
    this.userKey = userKey;
  }

}
