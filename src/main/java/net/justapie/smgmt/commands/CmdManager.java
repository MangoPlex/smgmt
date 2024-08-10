package net.justapie.smgmt.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.commands.moderation.Ban;

import java.util.Arrays;

public class CmdManager {
    private final ProxyServer proxy;

    public CmdManager(ProxyServer proxy) {
        this.proxy = proxy;

        this.registerCommand(
                new Ban()
        );
    }

    private void registerCommand(VCommand... cmdList) {
        Arrays.stream(cmdList).forEach(cmd -> {
            CommandMeta meta = this.proxy.getCommandManager()
                    .metaBuilder(cmd.name)
                    .aliases(cmd.aliases.toArray(new String[]{}))
                    .build();

            BrigadierCommand cmdToRegister = cmd.makeBrigadierCommand(this.proxy);

            this.proxy.getCommandManager().register(meta, cmdToRegister);
        });
    }
}
