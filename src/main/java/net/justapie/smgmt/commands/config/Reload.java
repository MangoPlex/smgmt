package net.justapie.smgmt.commands.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.commands.VCommand;
import net.justapie.smgmt.config.ConfigHelper;

import java.io.IOException;
import java.nio.file.Path;

public class Reload extends VCommand {
    private final Path dataDir;

    public Reload(Path dataDir) {
        super("smgmt");
        this.dataDir = dataDir;
    }

    @Override
    public BrigadierCommand makeBrigadierCommand(ProxyServer proxy) {
        LiteralCommandNode<CommandSource> cmdNode = BrigadierCommand.literalArgumentBuilder(this.name)
                .then(
                        BrigadierCommand.requiredArgumentBuilder("reload", StringArgumentType.word())
                                .suggests((ctx, builder) -> builder.suggest("reload").buildFuture())
                                .executes(ctx -> {
                                    try {
                                        ConfigHelper.getInstance().initializeConfig(dataDir);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ctx.getSource().sendPlainMessage("Error while reloading config");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    ctx.getSource().sendPlainMessage("Config reloaded successfully");
                                    return Command.SINGLE_SUCCESS;
                                })
                )
                .build();

        return new BrigadierCommand(cmdNode);
    }
}
