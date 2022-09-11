package us.blockgame.gravity.rank;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.command.RankCommand;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RankHandler {

    public RankHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new RankCommand());

        //Register listeners
        Bukkit.getPluginManager().registerEvents(new RankListener(), GravityPlugin.getInstance());

        loadPermissions();
    }

    private void loadPermissions() {
        CompletableFuture.runAsync(() -> {
            MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("ranks");

            Arrays.stream(Rank.values()).forEach(r -> {
                Document document = (Document) mongoCollection.find(Filters.eq("_id", r.toString())).first();

                if (document != null) {
                    List<String> permissions = (List<String>) document.get("permissions");

                    //Set permissions
                    r.setPermissions(permissions);
                }
            });
        });
    }

    public Rank getOfflineRank(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        //If player is online, don't need to get rank from Mongo
        if (player != null) {
            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
            GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

            return gravityProfile.getRank();
        }

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        //Get rank from Mongo
        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();
        if (document != null) {
            String rank = document.getString("rank");

            return Rank.getRank(rank);
        }
        return Rank.DEFAULT;
    }

    public Rank getOfflineVisibleRank(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        //If player is online, don't need to get rank from Mongo
        if (player != null) {
            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
            GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

            return gravityProfile.getUsedRank();
        }

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        //Get rank from Mongo
        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();
        if (document != null && document.containsKey("mask")) {
            String rank = document.getString("mask");

            return Rank.getRank(rank);
        }
        return getOfflineRank(uuid);
    }

    public void affectedPlayersOfRankOrAboveAction(Rank rank, Consumer<? super Player> action) {
        //Get all players with the same rank or rank above the rank provided
        Bukkit.getOnlinePlayers().stream().filter(p -> getOfflineRank(p.getUniqueId()).getWeight() <= rank.getWeight()).forEach(p -> {
            //Accept action
            action.accept(p);
        });
    }
}
