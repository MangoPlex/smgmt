package net.justapie.smgmt.database;

import dev.morphia.query.filters.Filters;
import net.justapie.smgmt.database.models.BanRecord;

import java.util.List;

public class MongoUtils {
  public static List<BanRecord> getRecords(String username) {
    return MongoHelper.getInstance().getDs().find(BanRecord.class)
      .filter(
        Filters.eq("username", username)
      )
      .stream()
      .sorted(
        (c1, c2) -> Math.toIntExact(c2.getBannedOn().getTime() - c1.getBannedOn().getTime())
      )
      .toList();
  }
}
