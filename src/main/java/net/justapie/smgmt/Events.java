package net.justapie.smgmt;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import dev.morphia.query.Query;
import net.justapie.smgmt.config.Config;
import net.justapie.smgmt.config.ConfigFormatter;
import net.justapie.smgmt.database.MongoHelper;
import net.justapie.smgmt.database.models.BanRecord;
import net.kyori.adventure.text.Component;

import java.util.Date;
import java.util.List;

public class Events {
    @Subscribe(order =  PostOrder.EARLY)
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();

        Query<BanRecord> recordQuery = MongoHelper.getInstance().getDs().find(BanRecord.class);

        List<BanRecord> records = recordQuery.stream()
                .sorted(
                        (c1, c2) -> Math.toIntExact(c2.getBannedOn().getTime() - c1.getBannedOn().getTime())
                )
                .toList();


        if (!records.isEmpty()) {
            BanRecord latestRecord = records.getFirst();
            String reason = Config.getMessageNode().node("ban").getString();

            if (!latestRecord.isPermanent())
                reason = Config.getMessageNode().node("tempBan").getString();

            if (latestRecord.isPermanent() || latestRecord.getBannedUntil().getTime() > new Date().getTime()) {
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
