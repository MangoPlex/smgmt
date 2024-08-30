package net.justapie.smgmt.database.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import net.justapie.smgmt.database.MongoHelper;
import org.bson.types.ObjectId;

@Entity
public class BaseModel {
  @Id
  private final ObjectId id = new ObjectId();

  public ObjectId getId() {
    return this.id;
  }

  public BaseModel submit() {
    MongoHelper.getInstance().getDs().insert(this);
    return this;
  }
}
