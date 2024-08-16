package net.justapie.smgmt.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.commands.config.Reload;
import net.justapie.smgmt.commands.moderation.Ban;
import net.justapie.smgmt.commands.moderation.Kick;
import net.justapie.smgmt.commands.moderation.Unban;

import java.nio.file.Path;
import java.util.Arrays;

public class CmdManager {
    private final ProxyServer proxy;
    private final Path dataDir;

    public CmdManager(ProxyServer proxy, Path dataDir) {
        this.proxy = proxy;
        this.dataDir = dataDir;

        this.registerCommand(
          new Ban(),
          new Reload(this.dataDir),
          new Kick(),
          new Unban()
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
