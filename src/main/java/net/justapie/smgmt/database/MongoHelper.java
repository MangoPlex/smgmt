package net.justapie.smgmt.database;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
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
        ds = Morphia.createDatastore(
                MongoClients.create(
                        databaseNode.node("mongodb-connection-string").getString()
                )
        );
    }

    public Datastore getDs() {
        return this.ds;
    }
}
