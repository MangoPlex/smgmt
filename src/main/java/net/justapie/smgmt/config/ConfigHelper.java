package net.justapie.smgmt.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHelper {
    private static final ConfigHelper instance = new ConfigHelper();
    private CommentedConfigurationNode config;

    public static ConfigHelper getInstance() {
        return instance;
    }

    public void initializeConfig(Path dataDirectory) throws IOException, ConfigurateException {
        if (Files.notExists(dataDirectory)) {
            Files.createDirectory(dataDirectory);
        }
        Path config = dataDirectory.resolve("config.yml");

        if (Files.notExists(config)) {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("config.yml");
            Files.copy(stream, config);
        }

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().file(config.toFile()).build();
        this.config = loader.load();
    }

    public CommentedConfigurationNode getConfig() {
        return this.config;
    }
}