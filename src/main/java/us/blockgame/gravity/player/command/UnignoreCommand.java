package us.blockgame.gravity.player.command;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.MapUtil;

import java.util.Map;
import java.util.UUID;

public class UnignoreCommand {

    @Command(name = "unignore", inGameOnly = true, runAsync = true)
    public void unignore(CommandArgs args) {
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
        Player player = args.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        if (!gravityProfile.getIgnored().contains(uuid)) {
            args.getSender().sendMessage(ChatColor.RED + "You are not ignoring " + cacheHandler.getUsername(uuid) + ".");
            return;
        }

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        Rank targetRank = rankHandler.getOfflineRank(uuid);

        //Add uuid to ignored
        gravityProfile.getIgnored().remove(uuid);

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first();

        Map<String, Object> documentMap = MapUtil.cloneDocument(document);
        documentMap.put("ignored", gravityProfile.getIgnored());

        //Delete old document
        mongoCollection.deleteOne(Filters.eq("_id", player.getUniqueId()));

        //Insert new document
        mongoCollection.insertOne(new Document(documentMap));

        args.getSender().sendMessage(ChatColor.YELLOW + "You are " + ChatColor.RED + "no longer" + ChatColor.YELLOW + " ignoring " + targetRank.getLitePrefix() + cacheHandler.getUsername(uuid) + ChatColor.YELLOW + ".");
        return;
    }
}
