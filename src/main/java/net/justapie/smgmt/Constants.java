package net.justapie.smgmt;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;

public class Constants {
  public static final List<String> DURATIONS = List.of("1d", "7d", "14d", "30d", "180d", "365d", "permanent");

  public static RequiredArgumentBuilder<CommandSource, String> getPlayerArg(ProxyServer proxy) {
    return BrigadierCommand.requiredArgumentBuilder(
      "player",
      StringArgumentType.word()
    ).suggests(
      (ctx, builder) -> {
        proxy.getAllPlayers().forEach(player -> builder.suggest(player.getUsername()));
        return builder.buildFuture();
      }
    );
  }

  public static RequiredArgumentBuilder<CommandSource, String> getReasonArg() {
    return BrigadierCommand.requiredArgumentBuilder(
      "reason",
      StringArgumentType.greedyString()
    );
  }

  public static RequiredArgumentBuilder<CommandSource, String> getDurationArgs() {
    return BrigadierCommand.requiredArgumentBuilder(
        "duration",
        StringArgumentType.word()
      )
      .suggests(
        (ctx, builder) -> {
          Constants.DURATIONS
            .forEach(builder::suggest);
          return builder.buildFuture();
        }
      );
  }
}
