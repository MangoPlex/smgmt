package net.justapie.smgmt.database.models;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import org.bson.types.ObjectId;

import java.math.BigInteger;
import java.util.Date;

@Entity
public class BanRecord {
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
            Date bannedUntil
    ) {
        this.id = id;
        this.username = username;
        this.reason = reason;
        this.isPermanent = isPermanent;
        this.bannedOn = bannedOn;
        this.bannedUntil = bannedUntil;
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

    public Date getBannedOn() {
        return bannedOn;
    }

    public Date getBannedUntil() {
        return bannedUntil;
    }
}
