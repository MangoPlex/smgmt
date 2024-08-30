package net.justapie.smgmt;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.justapie.smgmt.database.MongoUtils;
import net.justapie.smgmt.database.models.Record;
import net.justapie.smgmt.database.models.User;
import net.justapie.smgmt.enums.RecordType;
import net.justapie.smgmt.utils.config.Config;
import net.justapie.smgmt.utils.config.ConfigFormatter;
import net.kyori.adventure.text.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Events {
  @Subscribe(order = PostOrder.EARLY)
  public void onLogin(LoginEvent event) {
    Player player = event.getPlayer();

    if (Config.getAntiSpoofingNode().node("enabled").getString().equalsIgnoreCase("true")) {
      User user = MongoUtils.findPlayer(player.getUsername());
      if (Objects.isNull(user)) {
        new User()
          .setUsername(player.getUsername())
          .setJoinedOn(new Date())
          .submit();
      } else if (!player.getUsername().equals(user.getUsername())) {
        player.disconnect(
          Component.text(
            Config.getMessageNode().node("playerExist").getString()
          )
        );
      }
    }

    List<Record> records = MongoUtils.getRecords(player.getUsername(), RecordType.BAN);

    if (!records.isEmpty()) {
      Record latestRecord = records.getFirst();
      String reason = Config.getMessageNode().node("ban").getString();

      if (!latestRecord.isPermanent())
        reason = Config.getMessageNode().node("tempBan").getString();

      if (
        latestRecord.isPermanent() ||
          (Objects.isNull(latestRecord.getExpiredOn()) && latestRecord.getActiveUntil().getTime() > new Date().getTime())
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
                (latestRecord.getActiveUntil().getTime() - latestRecord.getCreatedOn().getTime()) / 864e+5 + " days"
              )
              .putKV(
                "unbanDate",
                latestRecord.getActiveUntil().toString()
              )
              .build()
          )
        );
      }
    }
  }

  @Subscribe(order = PostOrder.EARLY)
  public void onPlayerChat(PlayerChatEvent event) {
    Player player = event.getPlayer();

    List<Record> records = MongoUtils.getRecords(player.getUsername(), RecordType.MUTE);

    if (!records.isEmpty()) {
      Record latestRecord = records.getFirst();

      String reason = Config.getMessageNode().node("mute").getString();

      if (!latestRecord.isPermanent())
        reason = Config.getMessageNode().node("tempMute").getString();

      if (
        latestRecord.isPermanent() ||
          (Objects.isNull(latestRecord.getExpiredOn()) && latestRecord.getActiveUntil().getTime() > new Date().getTime())
      ) {
        player.sendMessage(
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
                (latestRecord.getActiveUntil().getTime() - latestRecord.getCreatedOn().getTime()) / 864e+5 + " days"
              )
              .putKV(
                "unmuteDate",
                latestRecord.getActiveUntil().toString()
              )
              .build()
          )
        );
        event.setResult(PlayerChatEvent.ChatResult.denied());
      }
    }
  }
}
