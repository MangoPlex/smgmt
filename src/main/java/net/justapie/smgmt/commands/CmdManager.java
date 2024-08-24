package net.justapie.smgmt.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.commands.config.Reload;
import net.justapie.smgmt.commands.moderation.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdManager {
  private static final List<BrigadierCommand> COMMANDS = new ArrayList<>();
  private final ProxyServer proxy;

  public CmdManager(ProxyServer proxy, Path dataDir) {
    this.proxy = proxy;

    this.registerCommand(
      new Ban(),
      new Reload(dataDir),
      new Kick(),
      new Unban(),
      new Mute(),
      new Unmute()
    );
  }

  public static List<BrigadierCommand> getCommands() {
    return COMMANDS;
  }

  private void registerCommand(VCommand... cmdList) {
    Arrays.stream(cmdList).forEach(cmd -> {
      CommandMeta meta = this.proxy.getCommandManager()
        .metaBuilder(cmd.name)
        .aliases(cmd.aliases.toArray(new String[]{}))
        .build();

      BrigadierCommand cmdToRegister = cmd.makeBrigadierCommand(this.proxy);

      COMMANDS.add(cmdToRegister);

      this.proxy.getCommandManager().register(meta, cmdToRegister);
    });
  }
}
