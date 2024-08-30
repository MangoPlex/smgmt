package net.justapie.smgmt.commands.moderation;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.Constants;
import net.justapie.smgmt.commands.VCommand;
import net.justapie.smgmt.database.MongoUtils;
import net.justapie.smgmt.database.models.Record;
import net.justapie.smgmt.enums.RecordType;
import net.justapie.smgmt.utils.Utils;
import net.justapie.smgmt.utils.config.Config;
import net.justapie.smgmt.utils.config.ConfigFormatter;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Mute extends VCommand {
  public Mute() {
    super("mute");
  }

  @Override
  public BrigadierCommand makeBrigadierCommand(ProxyServer proxy) {
    return new BrigadierCommand(
      BrigadierCommand.literalArgumentBuilder(this.name)
        .requires(src -> src.hasPermission("smgmt.moderation.mute"))
        .then(
          Constants.getPlayerArg(proxy)
            .then(
              Constants.getDurationArgs()
                .then(
                  Constants.getReasonArg()
                    .executes(
                      ctx -> {
                        String reason = ctx.getArgument("reason", String.class);
                        String username = ctx.getArgument("player", String.class);
                        String durString = ctx.getArgument("duration", String.class);
                        String muteMsg = Config.getMessageNode()
                          .node("mute")
                          .getString();

                        Date now = new Date();
                        if (!Constants.DURATIONS.contains(durString)) {
                          ctx.getSource().sendPlainMessage(
                            Config.getMessageNode().node("invalidDur").getString()
                          );

                          return Command.SINGLE_SUCCESS;
                        }

                        long duration = Utils.parseDuration(durString);

                        if (duration != 0) {
                          muteMsg = Config.getMessageNode()
                            .node("tempMute")
                            .getString();
                        }

                        Optional<Player> optionalPlayer = proxy.getPlayer(username);
                        if (optionalPlayer.isPresent()) {
                          Player player = optionalPlayer.get();
                          if (ctx.getSource() instanceof Player && player.getUniqueId().equals(((Player) ctx.getSource()).getUniqueId())) {
                            ctx.getSource().sendPlainMessage(
                              Config.getMessageNode().node("cannotMuteSelf").getString()
                            );
                            return Command.SINGLE_SUCCESS;
                          }
                        }

                        List<Record> records = MongoUtils.getRecords(username, RecordType.MUTE);

                        if (!records.isEmpty()) {
                          Record record = records.getFirst();
                          if (record.isPermanent() || (Objects.isNull(record.getExpiredOn()) && record.getActiveUntil().getTime() > now.getTime())) {
                            ctx.getSource().sendPlainMessage(
                              new ConfigFormatter(
                                Config.getMessageNode().node("alreadyMuted").getString()
                              )
                                .putKV("player", username)
                                .build()
                            );
                            return Command.SINGLE_SUCCESS;
                          }
                        }

                        new Record()
                          .setId(new ObjectId())
                          .setUsername(username)
                          .setType(RecordType.MUTE)
                          .setReason(reason)
                          .setPermanent(durString.equals("permanent"))
                          .setCreatedOn(now)
                          .setExpiredOn(new Date(now.getTime() + duration))
                          .setExpiredOn(null)
                          .submitRecord();

                        ctx.getSource().sendPlainMessage(
                          new ConfigFormatter(
                            muteMsg
                          )
                            .putKV("player", username)
                            .build()
                        );

                        return Command.SINGLE_SUCCESS;
                      }
                    )
                )
            )
        )
        .build()
    );
  }
}
