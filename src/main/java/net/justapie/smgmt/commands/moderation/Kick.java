package net.justapie.smgmt.commands.moderation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.commands.VCommand;
import net.justapie.smgmt.config.Config;
import net.justapie.smgmt.config.ConfigFormatter;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class Kick extends VCommand {
    public Kick() {
        super("kick");
    }

    @Override
    public BrigadierCommand makeBrigadierCommand(ProxyServer proxy) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder(this.name)
                        .then(
                                BrigadierCommand.requiredArgumentBuilder(
                                        "player",
                                        StringArgumentType.word()
                                ).suggests(
                                        (ctx, builder) -> {
                                            proxy.getAllPlayers().forEach(
                                                    val -> builder.suggest(val.getUsername())
                                            );
                                            return builder.buildFuture();
                                        }
                                ).then(
                                        BrigadierCommand.requiredArgumentBuilder(
                                                "reason",
                                                StringArgumentType.greedyString()
                                        ).executes(
                                                ctx -> {
                                                    String username = ctx.getArgument("player", String.class);
                                                    String reason = ctx.getArgument("reason", String.class);

                                                    Optional<Player> optionalPlayer = proxy.getPlayer(username);


                                                    optionalPlayer.ifPresentOrElse(
                                                            player -> player.disconnect(
                                                                    Component.text(
                                                                            new ConfigFormatter(
                                                                                    Config.getMessageNode().node("kick").getString()
                                                                            )
                                                                                    .putKV("reason", reason)
                                                                                    .build()
                                                                    )
                                                            ),
                                                            () -> {
                                                                ctx.getSource().sendPlainMessage(
                                                                        Config.getMessageNode().node("kickFailed").getString()
                                                                );
                                                            }
                                                    );

                                                    return Command.SINGLE_SUCCESS;
                                                }
                                        )
                                )
                        )
        );
    }
}
