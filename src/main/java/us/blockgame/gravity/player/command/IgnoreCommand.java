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
import us.blockgame.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreCommand {

    @Command(name = "ignore", inGameOnly = true, runAsync = true)
    public void ignore(CommandArgs args) {
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

        //Check if player is trying to ignore themself
        if (uuid.equals(player.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "You cannot ignore yourself.");
            return;
        }

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        if (gravityProfile.getIgnored().contains(uuid)) {
            args.getSender().sendMessage(ChatColor.RED + "You are already ignoring " + cacheHandler.getUsername(uuid) + ".");
            return;
        }

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        Rank targetRank = rankHandler.getOfflineRank(uuid);

        //Check if player is staff
        if (targetRank.isStaff()) {
            args.getSender().sendMessage(ChatColor.RED + "You may not ignore staff members.");
            return;
        }

        //Add uuid to ignored
        gravityProfile.getIgnored().add(uuid);

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first();

        Map<String, Object> documentMap = MapUtil.cloneDocument(document);
        documentMap.put("ignored", gravityProfile.getIgnored());

        //Delete old document
        mongoCollection.deleteOne(Filters.eq("_id", player.getUniqueId()));

        //Insert new document
        mongoCollection.insertOne(new Document(documentMap));

        args.getSender().sendMessage(ChatColor.YELLOW + "You are " + ChatColor.GREEN + "now" + ChatColor.YELLOW + " ignoring " + targetRank.getLitePrefix() + cacheHandler.getUsername(uuid) + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "ignore.list", inGameOnly = true)
    public void ignoreList(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player is ignoring anyone
        if (gravityProfile.getIgnored().size() == 0) {
            args.getSender().sendMessage(ChatColor.RED + "You are not ignoring anyone.");
            return;
        }
        List<String> names = new ArrayList<>();

        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

        gravityProfile.getIgnored().forEach(u -> {
            //Add player's colored name to the list of names
            names.add(rankHandler.getOfflineRank(u).getLitePrefix() + cacheHandler.getUsername(u));
        });

        args.getSender().sendMessage(ChatColor.YELLOW + "You are currently ignoring " + ChatColor.AQUA + gravityProfile.getIgnored().size() + ChatColor.YELLOW + " player" + (gravityProfile.getIgnored().size() == 1 ? "" : "s") + ".");
        args.getSender().sendMessage(ChatColor.YELLOW + "Ignored Players: " + ChatColor.RESET + StringUtil.join(names, ChatColor.GRAY + ", "));
        return;
    }
}
