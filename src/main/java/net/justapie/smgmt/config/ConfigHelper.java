package net.justapie.smgmt.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
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

    public Path createFile(Path dataDirectory, String fileName) throws IOException {
        if (Files.notExists(dataDirectory)) {
            Files.createDirectory(dataDirectory);
        }
        Path configFilePath = dataDirectory.resolve(fileName);

        if (Files.notExists(configFilePath)) {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName);
            Files.copy(stream, configFilePath);
        }
        return configFilePath;
    }

    public void initializeConfig(Path dataDirectory) throws IOException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(this.createFile(dataDirectory, "config.yml").toFile())
                .build();
        this.config = loader.load();
    }

    public CommentedConfigurationNode getConfig() {
        return this.config;
    }
}