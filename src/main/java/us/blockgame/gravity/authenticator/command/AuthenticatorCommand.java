package us.blockgame.gravity.authenticator.command;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.ChatColor;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.MapUtil;

import java.util.Map;
import java.util.UUID;

public class AuthenticatorCommand {

    @Command(name = "authenticator", aliases = {"auth"}, permission = "op")
    public void authenticator(CommandArgs args) {
        //Send authenticator help
        args.getSender().sendMessage(new String[]{
                ChatColor.RED + "Authenticator Help:",
                ChatColor.RED + " * /authenticator key <player>",
                ChatColor.RED + " * /authenticator disable <player>"
        });
    }

    @Command(name = "authenticator.key", aliases = {"auth.key"}, permission = "op", runAsync = true)
    public void authenticatorKey(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }
        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        //Make sure document exists
        if (document == null) {
            args.getSender().sendMessage(ChatColor.RED + "Data for this player was not found.");
            return;
        }
        if (!document.containsKey("gauth")) {
            args.getSender().sendMessage(ChatColor.RED + "That player does not have a 2FA key.");
            return;
        }
        String key = document.getString("gauth");

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

        args.getSender().sendMessage(rankHandler.getOfflineVisibleRank(uuid).getLitePrefix() + cacheHandler.getUsername(uuid) + "'s " + ChatColor.YELLOW + "key is " + ChatColor.AQUA + key + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "authenticator.disable", aliases = {"auth.disable"}, permission = "op", runAsync = true)
    public void authenticatorDisable(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }
        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        //Make sure document exists
        if (document == null) {
            args.getSender().sendMessage(ChatColor.RED + "Data for this player was not found.");
            return;
        }
        if (!document.containsKey("gauth-enabled") || !document.getBoolean("gauth-enabled")) {
            args.getSender().sendMessage(ChatColor.RED + "That player does not have 2FA enabled.");
            return;
        }
        Map<String, Object> documentMap = MapUtil.cloneDocument(document);
        documentMap.put("gauth-enabled", false);

        //Delete document
        mongoCollection.deleteOne((Document) mongoCollection.find(Filters.eq("_id", uuid)).first());

        //Insert new document
        mongoCollection.insertOne(new Document(documentMap));

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

        args.getSender().sendMessage(rankHandler.getOfflineVisibleRank(uuid).getLitePrefix() + cacheHandler.getUsername(uuid) + "'s " + ChatColor.YELLOW + "2FA has been " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "authenticator.enable", aliases = {"auth.enable"}, permission = "op", runAsync = true)
    public void authenticatorEnable(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }
        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        //Make sure document exists
        if (document == null) {
            args.getSender().sendMessage(ChatColor.RED + "Data for this player was not found.");
            return;
        }
        if (!document.containsKey("gauth-enabled")) {
            args.getSender().sendMessage(ChatColor.RED + "That player's 2FA cannot be enabled.");
            return;
        }
        if (document.getBoolean("gauth-enabled")) {
            args.getSender().sendMessage(ChatColor.RED + "That player's 2FA is already enabled.");
            return;
        }
        Map<String, Object> documentMap = MapUtil.cloneDocument(document);
        documentMap.put("gauth-enabled", true);

        //Delete document
        mongoCollection.deleteOne((Document) mongoCollection.find(Filters.eq("_id", uuid)).first());

        //Insert new document
        mongoCollection.insertOne(new Document(documentMap));

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

        args.getSender().sendMessage(rankHandler.getOfflineVisibleRank(uuid).getLitePrefix() + cacheHandler.getUsername(uuid) + "'s " + ChatColor.YELLOW + "2FA has been " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
        return;
    }
}
