package net.justapie.smgmt.database.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;

import java.util.Date;

@Entity
public class User extends BaseModel {
  @Property
  private String username;
  @Property
  private String lowerUsername;
  @Property
  private Date joinedOn;

  public User() {
  }

  public String getUsername() {
    return this.username;
  }

  public User setUsername(String username) {
    this.username = username;
    this.lowerUsername = username.toLowerCase();
    return this;
  }

  public Date getJoinedOn() {
    return joinedOn;
  }

  public User setJoinedOn(Date joinedOn) {
    this.joinedOn = joinedOn;
    return this;
  }

  public String getLowerUsername() {
    return this.lowerUsername;
  }
}
