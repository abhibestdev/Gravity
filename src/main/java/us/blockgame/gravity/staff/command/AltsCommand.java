package us.blockgame.gravity.staff.command;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
import us.blockgame.lib.util.CC;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AltsCommand {

    @Command(name = "alts", permission = "gravity.command.alts", runAsync = true)
    public void alts(CommandArgs args) {
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
        List<String> ips = (List<String>) document.get("ips");
        List<UUID> alts = new ArrayList<>();

        args.getSender().sendMessage(ChatColor.YELLOW + "Searching for alts. This may take a moment.");

        MongoCursor mongoCursor = mongoCollection.find().cursor();

        //Loop through all players in db
        while (mongoCursor.hasNext()) {
            Document playerDocument = (Document) mongoCursor.next();

            UUID playerUUID = (UUID) playerDocument.get("_id");

            if (!playerUUID.equals(uuid)) {
                List<String> playerIps = (List<String>) playerDocument.get("ips");

                for (String ip : ips) {
                    //If they share an ip, add as an alt
                    if (playerIps.contains(ip) && !alts.contains(playerUUID)) {
                        alts.add(playerUUID);
                        break;
                    }
                }
            }
        }
        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

        if (alts.size() > 0) {
            args.getSender().sendMessage(CC.GRAY + CC.STRIKE + "-----------------------------------");
            args.getSender().sendMessage(rankHandler.getOfflineRank(uuid).getLitePrefix() + cacheHandler.getUsername(uuid) + "'s " + ChatColor.GOLD + "alts: ");
            alts.stream().forEach(u -> {
                args.getSender().sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + cacheHandler.getUsername(u));
            });
            args.getSender().sendMessage(CC.GRAY + CC.STRIKE + "-----------------------------------");
        } else {
            args.getSender().sendMessage(ChatColor.RED + "No alts found for that player.");
        }
        return;
    }
}
