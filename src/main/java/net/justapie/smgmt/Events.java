package net.justapie.smgmt;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.justapie.smgmt.config.Config;
import net.justapie.smgmt.config.ConfigFormatter;
import net.justapie.smgmt.database.MongoUtils;
import net.justapie.smgmt.database.models.BanRecord;
import net.kyori.adventure.text.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Events {
  @Subscribe(order = PostOrder.EARLY)
  public void onLogin(LoginEvent event) {
    Player player = event.getPlayer();

    List<BanRecord> records = MongoUtils.getRecords(player.getUsername());

    if (!records.isEmpty()) {
      BanRecord latestRecord = records.getFirst();
      String reason = Config.getMessageNode().node("ban").getString();

      if (!latestRecord.isPermanent())
        reason = Config.getMessageNode().node("tempBan").getString();

      if (
        latestRecord.isPermanent() ||
          (Objects.isNull(latestRecord.getUnbannedOn()) && latestRecord.getBannedUntil().getTime() > new Date().getTime())
      ) {
        player.disconnect(
          Component.text(
            new ConfigFormatter(
              reason
            )
              .putKV(
                "reason",
                latestRecord.getReason()
              )
              .putKV(
                "duration",
                (latestRecord.getBannedUntil().getTime() - latestRecord.getBannedOn().getTime()) / 8.64e+7 + " days"
              )
              .putKV(
                "unbanDate",
                latestRecord.getBannedUntil().toString()
              )
              .build()
          )
        );
      }
    }
  }

}
