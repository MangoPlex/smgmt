package net.justapie.smgmt.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.config.MorphiaConfig;
import net.justapie.smgmt.utils.config.Config;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.conversions.Bson;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.concurrent.TimeUnit;

public class MongoHelper {
  private static final MongoHelper INSTANCE = new MongoHelper();
  private static final CommentedConfigurationNode databaseNode = Config.getDatabaseNode();
  private Datastore ds;
  private MongoClient mongoClient;

  public static MongoHelper getInstance() {
    return INSTANCE;
  }

  public void initializeDatabase() {
    this.ds = Morphia.createDatastore(
      this.mongoClient,
      MorphiaConfig.load().database("smgmt")
    );
  }

  public void testConnection() throws MongoException {
    this.mongoClient = MongoClients.create(
      MongoClientSettings.builder()
        .applyConnectionString(
          new ConnectionString(
            databaseNode.node("mongodb-connection-string").getString()
          )
        )
        .applyToSocketSettings(
          builder -> {
            builder.connectTimeout(100, TimeUnit.MILLISECONDS);
            builder.readTimeout(100, TimeUnit.MILLISECONDS);
          }
        )
        .applyToClusterSettings(
          builder ->
            builder.serverSelectionTimeout(100, TimeUnit.MILLISECONDS)
        )
        .applicationName("smgmt")
        .retryReads(true)
        .retryWrites(true)
        .build()
    );

    MongoDatabase database = mongoClient.getDatabase("admin");
    Bson command = new BsonDocument("ping", new BsonInt64(1));
    database.runCommand(command);
  }

  public Datastore getDs() {
    return this.ds;
  }
}
