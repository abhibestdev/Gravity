package us.blockgame.gravity.punishment.command;

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
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.fanciful.FancyMessage;
import us.blockgame.lib.redis.RedisHandler;
import us.blockgame.lib.util.MapUtil;
import us.blockgame.lib.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BlacklistCommand {

    @SneakyThrows
    @Command(name = "blacklist", permission = "gravity.command.blacklist", runAsync = true)
    public void blacklist(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <reason> <-s>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }
        String reason = StringUtil.buildString(args.getArgs(), 1);
        boolean silent = reason.contains("-s");

        if (silent) reason = reason.replace("-s", "").replace("  ", " ").trim();

        if (reason.replace(" ", "").equalsIgnoreCase(""))
            reason = "Misconduct";

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

        Map<String, Object> documentMap = MapUtil.cloneDocument(document);

        JSONObject jsonDocument = (JSONObject) new JSONParser().parse(document.toJson());
        JSONObject punishments = jsonDocument.containsKey("punishments") ? (JSONObject) jsonDocument.get("punishments") : new JSONObject();

        long blacklistTime = System.currentTimeMillis();

        //Create ban JSONObject
        JSONObject punishment = new JSONObject();
        punishment.put("reason", reason);
        punishment.put("type", "blacklist");
        punishment.put("uuid", cacheHandler.getUUID(args.getSender().getName()).toString());
        punishment.put("time", String.valueOf(blacklistTime));

        punishments.put(String.valueOf(blacklistTime), punishment);

        //Delete current document
        mongoCollection.deleteOne(document);

        documentMap.put("punishments", punishments);
        mongoCollection.insertOne(new Document(documentMap));

        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

        List<String> ips = (List<String>) document.get("ips");

        MongoCollection blacklistCollection = mongoHandler.getCollection("blacklists");
        for (String ip : ips) {

            Map<String, Object> blacklistMap = MapUtil.createMap(
                    "_id", ip,
                    "type", "blacklist",
                    "time", String.valueOf(blacklistTime),
                    "duration", "0",
                    "player", cacheHandler.getUsername(uuid),
                    "reason", reason);

            Document existingBlacklistforIp = (Document) blacklistCollection.find(Filters.eq("_id", ip)).first();

            //Delete existing blacklist for this ip
            if (existingBlacklistforIp != null) {
                blacklistCollection.deleteOne(existingBlacklistforIp);
            }

            //Insert new blacklist into mongo
            blacklistCollection.insertOne(new Document(blacklistMap));

            Map<String, Object> messageMap = MapUtil.createMap(
                    "type", "blacklist",
                    "player", cacheHandler.getUsername(uuid),
                    "ip", ip,
                    "reason", reason);

            //Send blacklist to redis
            redisHandler.send(messageMap);
        }

        //Broadcast blacklist
        FancyMessage fancyBanMessage = new FancyMessage(silent ? "(Silent) " : "").color(ChatColor.GRAY).then(senderRank.getLitePrefix() + args.getSender().getName()).then(" has blacklisted ").color(ChatColor.GREEN).then(playerRank.getLitePrefix() + cacheHandler.getUsername(uuid)).then(" for " + reason + ".").color(ChatColor.GREEN);
        Bukkit.getOnlinePlayers().stream().filter(p -> (silent ? p.hasPermission("gravity.silent") : true)).forEach(fancyBanMessage::send);
        return;
    }
}
