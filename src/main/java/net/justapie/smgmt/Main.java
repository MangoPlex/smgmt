package net.justapie.smgmt;

import com.google.inject.Inject;
import com.mongodb.MongoException;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.justapie.smgmt.commands.CmdManager;
import net.justapie.smgmt.config.ConfigHelper;
import net.justapie.smgmt.database.MongoHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@Plugin(
  id = "smgmt",
  name = "ServerManagement",
  description = "Proxy-wide moderation utilities for Velocity",
  authors = "JustAPie",
  version = BuildConstants.VERSION
)
public class Main {
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

      MongoHelper.getInstance().initializeDatabase();
    } catch (IOException | MongoException e) {
      if (e instanceof IOException) this.logger.error("Failed to load config. Shutting down");
      if (e instanceof MongoException) this.logger.error("Please specify mongodb connection string");
      logger.error(Arrays.toString(e.getStackTrace()));
      Optional<PluginContainer> container = this.proxy.getPluginManager().getPlugin("smgmt");
      container.ifPresent(ctx -> ctx.getExecutorService().shutdown());
      return;
    }

    this.logger.info("SMGMT is initialized");
  }

}
