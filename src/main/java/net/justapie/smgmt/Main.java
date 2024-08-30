package net.justapie.smgmt;

import com.google.inject.Inject;
import com.mongodb.MongoException;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.commands.CmdManager;
import net.justapie.smgmt.database.MongoHelper;
import net.justapie.smgmt.utils.config.ConfigHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

@Plugin(
  id = "smgmt",
  name = "ServerManagement",
  description = "Proxy-wide moderation utilities for Velocity",
  authors = "JustAPie",
  version = BuildConstants.VERSION
)
public final class Main {
  private final ProxyServer proxy;
  private final Logger logger;
  private final Path dataDirectory;

  @Inject
  public Main(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
    this.proxy = proxy;
    this.logger = logger;
    this.dataDirectory = dataDirectory;
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    this.proxy.getEventManager().register(this, new Events());
    new CmdManager(this.proxy, this.dataDirectory);

    try {
      ConfigHelper.getInstance().initializeConfig(this.dataDirectory);
    } catch (IOException e) {
      this.logger.error("Config file is malformed");
      this.logger.error(Arrays.toString(e.getStackTrace()));
    }

    try {
      MongoHelper.getInstance().testConnection();
      MongoHelper.getInstance().initializeDatabase();
    } catch (MongoException e) {
      this.logger.error("MongoDB database is unreachable");
      this.logger.error(Arrays.toString(e.getStackTrace()));
    }

    this.logger.info("SMGMT is initialized");
  }
}
