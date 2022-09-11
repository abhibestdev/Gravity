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
import us.blockgame.gravity.punishment.PunishmentHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.fanciful.FancyMessage;
import us.blockgame.lib.redis.RedisHandler;
import us.blockgame.lib.util.MapUtil;

import java.util.Map;
import java.util.UUID;

public class UnmuteCommand {

    @SneakyThrows
    @Command(name = "unmute", permission = "gravity.command.unmute", runAsync = true)
    public void unmute(CommandArgs args) {
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

        PunishmentHandler punishmentHandler = GravityPlugin.getInstance().getPunishmentHandler();
        JSONObject activeMute = punishmentHandler.getActiveMute(uuid);

        //Make sure player is muted
        if (activeMute == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not muted.");
            return;
        }

        activeMute.put("active", false);

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        Map<String, Object> documentMap = MapUtil.cloneDocument(document);

        JSONObject jsonDocument = (JSONObject) new JSONParser().parse(document.toJson());
        JSONObject punishments = jsonDocument.containsKey("punishments") ? (JSONObject) jsonDocument.get("punishments") : new JSONObject();

        //Replace punishment
        punishments.put(activeMute.get("time").toString(), activeMute);
        //Replace punishments
        documentMap.put("punishments", punishments);

        //Delete old document
        mongoCollection.deleteOne(document);

        //Insert new document
        mongoCollection.insertOne(new Document(documentMap));

        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

        Map<String, Object> messageMap = MapUtil.createMap(
                "type", "unmute",
                "player", cacheHandler.getUsername(uuid));

        //Send unmute to redis
        redisHandler.send(messageMap);

        //Broadcast unmute
        FancyMessage fancyBanMessage = new FancyMessage(silent ? "(Silent) " : "").color(ChatColor.GRAY).then(senderRank.getLitePrefix() + args.getSender().getName()).then(" has unmuted ").color(ChatColor.GREEN).then(playerRank.getLitePrefix() + cacheHandler.getUsername(uuid)).then(".").color(ChatColor.GREEN);
        Bukkit.getOnlinePlayers().stream().filter(p -> (silent ? p.hasPermission("gravity.silent") : true)).forEach(fancyBanMessage::send);

    }
}
