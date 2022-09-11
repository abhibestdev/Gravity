package us.blockgame.gravity.punishment.command;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.fanciful.FancyMessage;

import java.util.List;
import java.util.UUID;

public class UnblacklistCommand {

    @SneakyThrows
    @Command(name = "unblacklist", permission = "gravity.command.unblacklist", runAsync = true)
    public void blacklist(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <-s>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }
        boolean silent = args.length() > 1 && args.getArgs(1).equalsIgnoreCase("-s");

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

        //Get player's rank
        Rank playerRank = rankHandler.getOfflineRank(uuid);

        Rank senderRank = Rank.OWNER;
        if (args.getSender() instanceof Player) {
            senderRank = rankHandler.getOfflineRank(args.getPlayer().getUniqueId());
        }

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        List<String> ips = (List<String>) document.get("ips");

        MongoCollection blacklistCollection = mongoHandler.getCollection("blacklists");

        boolean unblacklisted = false;
        for (String ip : ips) {
            Document existingBlacklistforIp = (Document) blacklistCollection.find(Filters.eq("_id", ip)).first();

            //Delete existing blacklist for this ip
            if (existingBlacklistforIp != null) {
                unblacklisted = true;

                blacklistCollection.deleteOne(existingBlacklistforIp);
            }
        }
        if (!unblacklisted) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not blacklisted.");
            return;
        }
        //Broadcast unblacklist
        FancyMessage fancyBanMessage = new FancyMessage(silent ? "(Silent) " : "").color(ChatColor.GRAY).then(senderRank.getLitePrefix() + args.getSender().getName()).then(" has unblacklisted ").color(ChatColor.GREEN).then(playerRank.getLitePrefix() + cacheHandler.getUsername(uuid)).color(ChatColor.GREEN);
        Bukkit.getOnlinePlayers().stream().filter(p -> (silent ? p.hasPermission("gravity.silent") : true)).forEach(fancyBanMessage::send);
        return;
    }
}
