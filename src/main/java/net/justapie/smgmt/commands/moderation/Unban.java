package net.justapie.smgmt.commands.moderation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import net.justapie.smgmt.commands.VCommand;
import net.justapie.smgmt.config.Config;
import net.justapie.smgmt.config.ConfigFormatter;
import net.justapie.smgmt.database.MongoHelper;
import net.justapie.smgmt.database.MongoUtils;
import net.justapie.smgmt.database.models.Record;
import net.justapie.smgmt.enums.RecordType;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Unban extends VCommand {
  public Unban() {
    super("unban");
  }

  @Override
  public BrigadierCommand makeBrigadierCommand(ProxyServer proxy) {
    return new BrigadierCommand(
      BrigadierCommand.literalArgumentBuilder(this.name)
        .requires(src -> src.hasPermission("smgmt.moderation.unban"))
        .then(
          BrigadierCommand.requiredArgumentBuilder(
              "player",
              StringArgumentType.word()
            )
            .suggests(
              (ctx, builder) -> {
                proxy.getAllPlayers().forEach(
                  val -> builder.suggest(val.getUsername())
                );
                return builder.buildFuture();
              }
            ).executes(
              ctx -> {
                String username = ctx.getArgument("player", String.class);

                List<Record> records = MongoUtils.getRecords(username, RecordType.BAN);

                if (records.isEmpty()) {
                  ctx.getSource().sendPlainMessage(
                    new ConfigFormatter(
                      Config.getMessageNode().node("noRecord").getString()
                    )
                      .putKV("player", username)
                      .build()
                  );
                  return Command.SINGLE_SUCCESS;
                }

                Record latestRecord = records.getFirst();

                if (!Objects.isNull(latestRecord.getExpiredOn()) || (!latestRecord.isPermanent() && latestRecord.getActiveUntil().getTime() < new Date().getTime())) {
                  ctx.getSource().sendPlainMessage(
                    new ConfigFormatter(
                      Config.getMessageNode().node("noRecord").getString()
                    )
                      .putKV("player", username)
                      .build()
                  );
                  return Command.SINGLE_SUCCESS;
                }

                if (latestRecord.isPermanent()) {
                  latestRecord.setPermanent(false);
                } else {
                  latestRecord.setExpiredOn(new Date());
                }

                MongoHelper.getInstance().getDs().find(Record.class)
                  .filter(
                    Filters.eq("_id", latestRecord.getId())
                  )
                  .update(
                    UpdateOperators.set("isPermanent", latestRecord.isPermanent()),
                    UpdateOperators.set("expiredOn", latestRecord.getExpiredOn())
                  );

                ctx.getSource().sendPlainMessage(
                  new ConfigFormatter(
                    Config.getMessageNode().node("unbanned").getString()
                  )
                    .putKV("player", username)
                    .build()
                );

                return Command.SINGLE_SUCCESS;
              }
            )
        )
    );
  }
}
