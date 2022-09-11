package us.blockgame.gravity.punishment;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.punishment.command.*;
import us.blockgame.gravity.punishment.impl.MutePunishment;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.util.MapUtil;
import us.blockgame.lib.util.TimeUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PunishmentHandler {

    public PunishmentHandler() {
        //Register Commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new BanCommand());
        commandHandler.registerCommand(new MuteCommand());
        commandHandler.registerCommand(new KickCommand());
        commandHandler.registerCommand(new BlacklistCommand());
        commandHandler.registerCommand(new UnbanCommand());
        commandHandler.registerCommand(new UnmuteCommand());
        commandHandler.registerCommand(new UnblacklistCommand());
        commandHandler.registerCommand(new HistoryCommand());

        //Register Listeners
        Bukkit.getPluginManager().registerEvents(new PunishmentListener(), GravityPlugin.getInstance());
    }

    public String getFormattedBanReason(String reason, long banTime, long duration, boolean ban) {
        return ChatColor.RED + "You are currently " + (ban ? "banned" : "blacklisted") + " from this server." + "\n" +
                ChatColor.RED + "Reason: " + ChatColor.GRAY + reason + ChatColor.RED + "\n" +
                ChatColor.RED + (ban ? "Expires: " + ChatColor.GRAY + (duration == -1L ? "Never" : TimeUtil.formatTimeMillis((duration + banTime) - System.currentTimeMillis(), false, true)) : "This type of punishment cannot be appealed.");
    }

    public String getFormattedKickReason(String reason) {
        return ChatColor.RED + "You have been kicked from this server." + "\n" +
                ChatColor.RED + "Reason: " + ChatColor.GRAY + reason;
    }

    @SneakyThrows
    public JSONObject getActiveBanOrBlacklist(UUID uuid) {
        JSONObject blacklistObject = getActiveBlacklist(uuid);

        //If player is blacklisted, return that
        if (blacklistObject != null) return blacklistObject;

        JSONObject banObject = getActiveBan(uuid);

        //Return ban
        if (banObject != null) return banObject;

        //Not punished
        return null;
    }

    @SneakyThrows
    public JSONObject getActiveBlacklist(UUID uuid) {
        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        JSONObject activeBan = null;

        if (document != null) {
            List<String> ips = (List<String>) document.get("ips");

            MongoCollection blacklistCollection = mongoHandler.getCollection("blacklists");
            for (String ip : ips) {
                Document blacklistDocument = (Document) blacklistCollection.find(Filters.eq("_id", ip)).first();

                if (blacklistDocument != null) {
                    activeBan = (JSONObject) new JSONParser().parse(blacklistDocument.toJson());
                    break;
                }
            }
        }
        return activeBan;
    }

    @SneakyThrows
    public JSONObject getActiveMute(UUID uuid) {
        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        JSONObject jsonDocument = (JSONObject) new JSONParser().parse(document.toJson());

        //Check if player has punishments
        if (!jsonDocument.containsKey("punishments")) return null;

        JSONObject punishments = (JSONObject) jsonDocument.get("punishments");

        long banTime = 0L;
        JSONObject activeBan = null;
        boolean active = false;
        boolean expired = false;

        for (Object o : punishments.keySet()) {

            long time = Long.parseLong(o.toString());

            JSONObject punishment = (JSONObject) punishments.get(o);
            String type = (String) punishment.get("type");

            //Ignore anything but mutes
            if (!type.equalsIgnoreCase("mute")) continue;

            active = (boolean) punishment.get("active");
            long duration = Long.parseLong((String) punishment.get("duration"));
            expired = duration != -1L && (time + duration < System.currentTimeMillis());

            //If the ban says its active, but the ban is expired, mark it as inactive
            if (active && expired) {
                Map<String, Object> documentMap = MapUtil.cloneDocument(document);

                //Make clone punishment we can update
                JSONObject punishmentClone = (JSONObject) punishment.clone();
                punishmentClone.put("active", false);

                //Replace punishment with cloned version
                punishments.put(String.valueOf(time), punishmentClone);

                //Place punishments back in the document
                documentMap.put("punishments", punishments);

                //Delete old document
                mongoCollection.deleteOne(document);

                //Insert new document
                mongoCollection.insertOne(new Document(documentMap));
                active = false;
            }

            //Make sure found punishment is a mute and that it is active
            if (active) {
                activeBan = punishment;
                banTime = time;
                break;
            }
        }
        return activeBan;
    }

    @SneakyThrows
    public JSONObject getActiveBan(UUID uuid) {
        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        JSONObject jsonDocument = (JSONObject) new JSONParser().parse(document.toJson());

        //Check if player has punishments
        if (!jsonDocument.containsKey("punishments")) return null;

        JSONObject punishments = (JSONObject) jsonDocument.get("punishments");

        long banTime = 0L;
        JSONObject activeBan = null;
        boolean active = false;
        boolean expired = false;

        for (Object o : punishments.keySet()) {

            long time = Long.parseLong(o.toString());

            JSONObject punishment = (JSONObject) punishments.get(o);
            String type = (String) punishment.get("type");

            //Ignore anything other than bans
            if (!type.equalsIgnoreCase("ban")) continue;

            active = (boolean) punishment.get("active");
            long duration = Long.parseLong((String) punishment.get("duration"));
            expired = duration != -1L && (time + duration < System.currentTimeMillis());

            //If the ban says its active, but the ban is expired, mark it as inactive
            if (active && expired) {
                Map<String, Object> documentMap = MapUtil.cloneDocument(document);

                //Make clone punishment we can update
                JSONObject punishmentClone = (JSONObject) punishment.clone();
                punishmentClone.put("active", false);

                //Replace punishment with cloned version
                punishments.put(String.valueOf(time), punishmentClone);

                //Place punishments back in the document
                documentMap.put("punishments", punishments);

                //Delete old document
                mongoCollection.deleteOne(document);

                //Insert new document
                mongoCollection.insertOne(new Document(documentMap));
                active = false;
            }

            //Make sure found punishment is a mute and that it is active
            if (active) {
                activeBan = punishment;
                banTime = time;
                break;
            }
        }
        return activeBan;
    }

    public boolean isMuted(Player player) {
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player is muted
        if (gravityProfile.getMutePunishment() != null) {
            MutePunishment mutePunishment = gravityProfile.getMutePunishment();

            //Check if mute is perms
            if (mutePunishment.getDuration() == -1L) {
                return true;
            }
            //Make sure mute isn't expired
            if (mutePunishment.getDuration() + mutePunishment.getMuteTime() > System.currentTimeMillis()) {
                return true;
            }
            gravityProfile.setMutePunishment(null);
            return false;
        }
        return false;
    }
}
