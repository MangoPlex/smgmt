package net.justapie.smgmt.database.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity
public class BanRecord {
  @Property
  Date unbannedOn;
  @Id
  private ObjectId id;
  @Property
  private String username;
  @Property
  private String reason;
  @Property
  private boolean isPermanent;
  @Property
  private Date bannedOn;
  @Property
  private Date bannedUntil;

  public BanRecord() {

  }

  public BanRecord(
    ObjectId id,
    String username,
    String reason,
    boolean isPermanent,
    Date bannedOn,
    Date bannedUntil,
    Date unbannedOn
  ) {
    this.id = id;
    this.username = username;
    this.reason = reason;
    this.isPermanent = isPermanent;
    this.bannedOn = bannedOn;
    this.bannedUntil = bannedUntil;
    this.unbannedOn = unbannedOn;
  }

  public ObjectId getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getReason() {
    return reason;
  }

  public boolean isPermanent() {
    return isPermanent;
  }

  public void setPermanent(boolean permanent) {
    isPermanent = permanent;
  }

  public Date getBannedOn() {
    return bannedOn;
  }

  public Date getBannedUntil() {
    return bannedUntil;
  }

  public void setBannedUntil(Date bannedUntil) {
    this.bannedUntil = bannedUntil;
  }

  public Date getUnbannedOn() {
    return unbannedOn;
  }

  public void setUnbannedOn(Date unbannedOn) {
    this.unbannedOn = unbannedOn;
  }
}
