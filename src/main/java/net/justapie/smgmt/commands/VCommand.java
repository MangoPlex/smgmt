package net.justapie.smgmt.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;

public abstract class VCommand {
    public String name;
    public List<String> aliases = List.of();

    public VCommand(String name) {
        this.name = name;
    }

    public VCommand(String name, List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract BrigadierCommand makeBrigadierCommand(ProxyServer proxy);
}
