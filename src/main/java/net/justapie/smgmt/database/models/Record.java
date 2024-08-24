package net.justapie.smgmt.database.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import net.justapie.smgmt.enums.RecordType;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity
public class Record {
  @Id
  private ObjectId id;
  @Property
  private String username;
  @Property
  Date expiredOn;
  @Property
  private RecordType type;
  @Property
  private boolean isPermanent;
  @Property
  private String reason;
  @Property
  private Date createdOn;
  @Property
  private Date activeUntil;

  public Record() {

  }

  public Record(
    ObjectId id,
    String username,
    RecordType type,
    String reason,
    boolean isPermanent,
    Date createdOn,
    Date activeUntil,
    Date expiredOn
  ) {
    this.id = id;
    this.username = username;
    this.type = type;
    this.reason = reason;
    this.isPermanent = isPermanent;
    this.createdOn = createdOn;
    this.activeUntil = activeUntil;
    this.expiredOn = expiredOn;
  }

  public ObjectId getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public RecordType getType() {
    return this.type;
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

  public Date getCreatedOn() {
    return this.createdOn;
  }

  public Date getActiveUntil() {
    return this.activeUntil;
  }

  public void setActiveUntil(Date activeUntil) {
    this.activeUntil = activeUntil;
  }

  public Date getExpiredOn() {
    return this.expiredOn;
  }

  public void setExpiredOn(Date expiredOn) {
    this.expiredOn = expiredOn;
  }
}
