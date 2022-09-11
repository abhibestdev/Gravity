package us.blockgame.gravity.staff.command;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.ChatColor;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.DateUtil;
import us.blockgame.lib.util.TimeUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerInfoCommand {

    @Command(name = "playerinfo", aliases = {"pi"}, permission = "op")
    public void playerInfo(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        //Make sure player has logged in before
        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }

        CompletableFuture.runAsync(() -> {
            MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("players");

            Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

            //Make sure player has existing player data
            if (document == null) {
                args.getSender().sendMessage(ChatColor.RED + "Data for this player was not found.");
                return;
            }
            RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
            Rank rank = rankHandler.getOfflineRank(uuid);

            long firstLogin = document.getLong("firstLogin");
            long lastLogin = document.getLong("lastLogin");
            long lastLogout = document.getLong("lastLogout");

            args.getSender().sendMessage(new String[]{
                    ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------",
                    ChatColor.BLUE + "Rank: " + rank.getLitePrefix() + rank.getDisplayName(),
                    ChatColor.BLUE + "First Login: " + ChatColor.RESET + DateUtil.millisToDate(firstLogin),
                    ChatColor.BLUE + "Status: " + ChatColor.RESET + (lastLogin > lastLogout ? "Online for " + TimeUtil.formatTimeMillis(System.currentTimeMillis() - lastLogin, false, true) : "Last seen " + TimeUtil.formatTimeMillis(System.currentTimeMillis() - lastLogout, false, true) + " ago"),
                    ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------"
            });
        });
        return;
    }
}
