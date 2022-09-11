package us.blockgame.gravity.grant.command;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.grant.Grant;
import us.blockgame.gravity.grant.menu.grant.GrantMenu;
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
import us.blockgame.lib.util.TimeUtil;

import java.util.Map;
import java.util.UUID;

public class GrantCommand {

    @Command(name = "grant", permission = "gravity.command.grant", runAsync = true)
    public void grant(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <rank> <duration> <reason>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }

        if (args.length() == 1) {
            GrantMenu grantMenu = new GrantMenu(uuid);
            grantMenu.openMenu(args.getPlayer());
            return;
        }
        Rank rank = Rank.getRank(args.getArgs(1));

        //Check if rank exists
        if (rank == null) {
            args.getSender().sendMessage(ChatColor.RED + "That rank doesn't exist.");
            return;
        }
        long time = args.getArgs().length > 2 ? TimeUtil.parseTime(args.getArgs(2)) : -1L;
        String reason = (args.getArgs().length > 2 ? StringUtil.buildString(args.getArgs(), time == -1L ? 2 : 3) : "").replace("  ", "");

        if (reason.equalsIgnoreCase("")) {
            reason = "No Reason";
        }
        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        //Check if the player's document exists
        if (document == null) {
            args.getSender().sendMessage(ChatColor.RED + "Data for this player was not found.");
            return;
        }

        Map<String, Object> documentMap = MapUtil.cloneDocument(document);

        JSONObject jsonDocument = null;
        try {
            jsonDocument = (JSONObject) new JSONParser().parse(document.toJson());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject grants = jsonDocument.containsKey("grants") ? (JSONObject) jsonDocument.get("grants") : new JSONObject();

        long grantTime = System.currentTimeMillis();

        //Create grant JSONObject
        JSONObject grant = new JSONObject();
        grant.put("rank", rank.getName());
        grant.put("reason", reason);
        grant.put("duration", String.valueOf(time));
        grant.put("uuid", cacheHandler.getUUID(args.getSender().getName()).toString());

        grants.put(String.valueOf(grantTime), grant);

        //Delete current document
        mongoCollection.deleteOne(document);

        documentMap.put("grants", grants);
        documentMap.put("rank", rank.getName());

        //Input new document into mongo
        mongoCollection.insertOne(new Document(documentMap));

        Player target = Bukkit.getPlayer(uuid);

        if (target != null) {
            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
            GravityProfile gravityProfile = profileHandler.getGravityProfile(target);

            RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

            //Add grant to grants list and the grants task will automatically update the players rank
            gravityProfile.getGrantList().add(new Grant(grantTime, time, reason, rank, uuid, rankHandler.getOfflineRank(cacheHandler.getUUID(args.getSender().getName()))));
        }

        args.getSender().sendMessage(ChatColor.YELLOW + "You have granted " + rank.getLitePrefix() + cacheHandler.getUsername(uuid) + ChatColor.YELLOW + " the " + rank.getLitePrefix() + rank.getDisplayName() + ChatColor.YELLOW + " for " + ChatColor.GOLD + TimeUtil.formatTimeMillis(time, false, true) + ChatColor.YELLOW + " for " + ChatColor.GOLD + reason + ChatColor.YELLOW + ".");
        return;
    }
}
