package net.justapie.smgmt.commands.moderation;

import com.mojang.brigadier.Command;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.Constants;
import net.justapie.smgmt.commands.VCommand;
import net.justapie.smgmt.config.Config;
import net.justapie.smgmt.config.ConfigFormatter;
import net.justapie.smgmt.database.MongoHelper;
import net.justapie.smgmt.database.MongoUtils;
import net.justapie.smgmt.database.models.Record;
import net.justapie.smgmt.enums.RecordType;
import net.kyori.adventure.text.Component;
import org.bson.types.ObjectId;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Ban extends VCommand {
  public Ban() {
    super("ban");
  }

  @Override
  public BrigadierCommand makeBrigadierCommand(ProxyServer proxy) {
    return new BrigadierCommand(
      BrigadierCommand.literalArgumentBuilder(this.name)
        .requires(src -> src.hasPermission("smgmt.moderation.ban"))
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
                        String banMsg = Config.getMessageNode()
                          .node("ban")
                          .getString();

                        Date now = new Date();
                        if (!Constants.DURATIONS.contains(durString)) {
                          ctx.getSource().sendPlainMessage(
                            Config.getMessageNode().node("invalidDur").getString()
                          );

                          return Command.SINGLE_SUCCESS;
                        }

                        long duration = 0;
                        try {
                          duration = Duration.parse("P" + durString).toMillis();
                        } catch (ArithmeticException |
                                 DateTimeParseException ignored) {
                        }

                        if (duration != 0) {
                          banMsg = Config.getMessageNode()
                            .node("tempBan")
                            .getString();
                        }

                        Optional<Player> optionalPlayer = proxy.getPlayer(username);
                        if (optionalPlayer.isPresent()) {
                          Player player = optionalPlayer.get();
                          if (ctx.getSource() instanceof Player && player.getUniqueId().equals(((Player) ctx.getSource()).getUniqueId())) {
                            ctx.getSource().sendPlainMessage(
                              Config.getMessageNode().node("cannotBanSelf").getString()
                            );
                            return Command.SINGLE_SUCCESS;
                          }


                          player.disconnect(
                            Component.text(
                              new ConfigFormatter(
                                banMsg
                              )
                                .putKV(
                                  "reason",
                                  Objects.isNull(reason) ?
                                    Config.getDatabaseNode().node("noReason").getString() :
                                    reason
                                )
                                .putKV(
                                  "duration",
                                  durString
                                )
                                .putKV(
                                  "unbanDate",
                                  new Date(now.getTime() + duration).toString()
                                )
                                .build()
                            )
                          );
                        }

                        List<Record> records = MongoUtils.getRecords(username, RecordType.BAN);

                        if (!records.isEmpty()) {
                          Record record = records.getFirst();

                          if (record.isPermanent() || (Objects.isNull(record.getExpiredOn()) && record.getActiveUntil().getTime() > now.getTime())) {
                            ctx.getSource().sendPlainMessage(
                              new ConfigFormatter(
                                Config.getMessageNode().node("alreadyBanned").getString()
                              )
                                .putKV("player", username)
                                .build()
                            );
                            return Command.SINGLE_SUCCESS;
                          }
                        }

                        MongoHelper.getInstance().getDs().insert(
                          new Record(
                            new ObjectId(),
                            username,
                            RecordType.BAN,
                            reason,
                            durString.equals("permanent"),
                            now,
                            new Date(now.getTime() + duration),
                            null
                          )
                        );

                        ctx.getSource().sendPlainMessage(
                          new ConfigFormatter(
                            Config.getMessageNode().node("playerBanned").getString()
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