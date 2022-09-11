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
import us.blockgame.lib.util.TimeUtil;

import java.util.Map;
import java.util.UUID;

public class BanCommand {

    @SneakyThrows
    @Command(name = "ban", permission = "gravity.command.ban", runAsync = true)
    public void ban(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <time> <reason> <-s>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }
        long time = args.getArgs().length > 1 ? TimeUtil.parseTime(args.getArgs(1)) : -1L;
        String reason = args.getArgs().length > 1 ? StringUtil.buildString(args.getArgs(), time == -1L ? 1 : 2) : "";
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

        long banTime = System.currentTimeMillis();

        //Create ban JSONObject
        JSONObject punishment = new JSONObject();
        punishment.put("reason", reason);
        punishment.put("duration", String.valueOf(time));
        punishment.put("type", "ban");
        punishment.put("active", true);
        punishment.put("uuid", cacheHandler.getUUID(args.getSender().getName()).toString());
        punishment.put("time", String.valueOf(banTime));

        punishments.put(String.valueOf(banTime), punishment);

        //Delete current document
        mongoCollection.deleteOne(document);

        documentMap.put("punishments", punishments);
        mongoCollection.insertOne(new Document(documentMap));

        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

        Map<String, Object> messageMap = MapUtil.createMap(
                "type", "ban",
                "player", cacheHandler.getUsername(uuid),
                "reason", reason,
                "duration", time,
                "time", banTime);

        //Send ban to redis
        redisHandler.send(messageMap);

        //Broadcast ban
        FancyMessage fancyBanMessage = new FancyMessage(silent ? "(Silent) " : "").color(ChatColor.GRAY).then(senderRank.getLitePrefix() + args.getSender().getName()).then(" has " + (time == -1L ? "permanently" : "temporarily") + " banned ").color(ChatColor.GREEN).then(playerRank.getLitePrefix() + cacheHandler.getUsername(uuid)).then(" for " + (time > -1L ? TimeUtil.formatTimeMillis(time, false, true) + " for " : "") + reason + ".").color(ChatColor.GREEN);
        Bukkit.getOnlinePlayers().stream().filter(p -> (silent ? p.hasPermission("gravity.silent") : true)).forEach(fancyBanMessage::send);
        return;
    }
}
