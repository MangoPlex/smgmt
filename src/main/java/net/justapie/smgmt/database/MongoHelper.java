package net.justapie.smgmt.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.config.MorphiaConfig;
import net.justapie.smgmt.config.Config;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class MongoHelper {
    private static final MongoHelper instance = new MongoHelper();
    private static final CommentedConfigurationNode databaseNode = Config.getDatabaseNode();
    private Datastore ds;

    public static MongoHelper getInstance() {
        return instance;
    }

    public void initializeDatabase() {
        this.ds = Morphia.createDatastore(
                MongoClients.create(
                        MongoClientSettings.builder()
                                .applyConnectionString(
                                        new ConnectionString(
                                                databaseNode.node("mongodb-connection-string").getString()
                                        )
                                )
                                .applicationName("smgmt")
                                .retryReads(true)
                                .retryWrites(true)
                                .build()
                ),
                MorphiaConfig.load().database("smgmt")
        );

    }

    public Datastore getDs() {
        return this.ds;
    }
}
