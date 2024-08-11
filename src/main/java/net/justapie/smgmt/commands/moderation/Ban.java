package net.justapie.smgmt.commands.moderation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.commands.VCommand;
import net.justapie.smgmt.config.Config;
import net.justapie.smgmt.config.ConfigFormatter;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class Ban extends VCommand {
    public Ban() {
        super("ban");
    }

    @Override
    public BrigadierCommand makeBrigadierCommand(ProxyServer proxy) {
        LiteralCommandNode<CommandSource> banNode = BrigadierCommand.literalArgumentBuilder(this.name)
                .then(
                        BrigadierCommand.requiredArgumentBuilder(
                                "player",
                                StringArgumentType.word()
                        ).suggests(
                                (ctx, builder) -> {
                                    proxy.getAllPlayers().forEach(player -> builder.suggest(player.getUsername()));
                                    return builder.buildFuture();
                                }
                        ).executes(
                                ctx -> {
                                    Optional<Player> player = proxy.getPlayer(ctx.getArgument("player", String.class));
                                    if (player.isPresent()) {
                                        if (ctx.getSource() instanceof Player && player.get().getUniqueId().equals(((Player) ctx.getSource()).getUniqueId())) {
                                            ctx.getSource().sendPlainMessage("You cannot ban yourself");
                                            return Command.SINGLE_SUCCESS;
                                        }

                                        player.get().disconnect(
                                                Component.text(
                                                        new ConfigFormatter(
                                                                Config.getMessageNode()
                                                                        .node("ban")
                                                                        .getString()
                                                        )
                                                                .putKV("reason", "No Reason Provided")
                                                                .build()
                                                )
                                        );
                                    }
                                    ctx.getSource().sendPlainMessage("Player banned");
                                    return Command.SINGLE_SUCCESS;
                                }
                        )
                )
                .build();

        return new BrigadierCommand(banNode);
    }
}
