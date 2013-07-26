package com.project.client;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.project.shared.entities.User;

public class StoredUsers implements Serializable {
  private Set<User> users = new HashSet<User>();

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

}
