package net.justapie.smgmt.commands.moderation;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.commands.VCommand;

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
                        ).build()
                )
                .executes(ctx -> {
                    ctx.getSource().sendPlainMessage(ctx.getArgument("player", String.class));
                    return Command.SINGLE_SUCCESS;
                })
                .build();

        return new BrigadierCommand(banNode);
    }
}
