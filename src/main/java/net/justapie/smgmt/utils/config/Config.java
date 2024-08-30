package net.justapie.smgmt.utils.config;

import org.spongepowered.configurate.CommentedConfigurationNode;

public class Config {
  public static CommentedConfigurationNode getMessageNode() {
    return ConfigHelper.getInstance().getConfig().node("messages");
  }

  public static CommentedConfigurationNode getDatabaseNode() {
    return ConfigHelper.getInstance().getConfig().node("database");
  }
}
