package us.blockgame.gravity.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.util.Logger;

public class MongoHandler {

    @Getter private MongoClient mongoClient;
    @Getter private MongoDatabase mongoDatabase;

    public MongoHandler() {
        String ip = LibPlugin.getInstance().getConfig().getString("mongo.ip");
        int port = LibPlugin.getInstance().getConfig().getInt("mongo.port");
        boolean usePassword = LibPlugin.getInstance().getConfig().getBoolean("mongo.use-password");
        String username = LibPlugin.getInstance().getConfig().getString("mongo.username");
        String password = LibPlugin.getInstance().getConfig().getString("mongo.password");

        try {
            String mongoUri = "mongodb://" + (usePassword ? username + ":" + password + "@" : "") + ip + ":" + port;
            MongoClientURI uri = new MongoClientURI(mongoUri, MongoClientOptions.builder().maxWaitTime(30000).maxConnectionIdleTime(5000).threadsAllowedToBlockForConnectionMultiplier(500));

            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase("Gravity");
            mongoClient.getAddress();
            Logger.success("Successfully established Mongo connection.");
        } catch (Exception ex) {
            Logger.error("Could not establish Mongo connection.");
        }
    }

    public MongoCollection getCollection(String collection) {
        return mongoDatabase.getCollection(collection);
    }
}
