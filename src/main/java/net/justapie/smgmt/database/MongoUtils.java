package net.justapie.smgmt.database;

import dev.morphia.query.filters.Filters;
import net.justapie.smgmt.database.models.Record;
import net.justapie.smgmt.enums.RecordType;

import java.util.List;

public class MongoUtils {
  public static List<Record> getRecords(String username, RecordType type) {
    return MongoHelper.getInstance().getDs().find(Record.class)
      .filter(
        Filters.eq("username", username),
        Filters.eq("type", type)
      )
      .stream()
      .sorted(
        (c1, c2) -> Math.toIntExact(c2.getCreatedOn().getTime() - c1.getCreatedOn().getTime())
      )
      .toList();
  }
}
