package net.justapie.smgmt.database.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import net.justapie.smgmt.database.MongoHelper;
import net.justapie.smgmt.enums.RecordType;

import java.util.Date;

@Entity
public class Record extends BaseModel {
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

  public RecordType getType() {
    return this.type;
  }

  public String getUsername() {
    return this.username;
  }

  public Record setUsername(String username) {
    this.username = username;
    return this;
  }

  public Date getCreatedOn() {
    return this.createdOn;
  }

  public Date getActiveUntil() {
    return this.activeUntil;
  }

  public Date getExpiredOn() {
    return this.expiredOn;
  }

  public String getReason() {
    return this.reason;
  }

  public Record setReason(String reason) {
    this.reason = reason;
    return this;
  }

  public boolean isPermanent() {
    return this.isPermanent;
  }

  public Record setPermanent(boolean permanent) {
    this.isPermanent = permanent;
    return this;
  }

  public Record setType(RecordType type) {
    this.type = type;
    return this;
  }

  public Record setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
    return this;
  }

  public Record setActiveUntil(Date activeUntil) {
    this.activeUntil = activeUntil;
    return this;
  }

  public Record setExpiredOn(Date expiredOn) {
    this.expiredOn = expiredOn;
    return this;
  }

  public void deactivateRecord() {
    if (this.isPermanent()) this.setPermanent(false);
    else this.setActiveUntil(new Date());

    MongoHelper.getInstance().getDs().find(this.getClass())
      .filter(
        Filters.eq("_id", this.getId())
      )
      .update(
        UpdateOperators.set("isPermanent", this.isPermanent()),
        UpdateOperators.set("expiredOn", this.getExpiredOn())
      );
  }
}
