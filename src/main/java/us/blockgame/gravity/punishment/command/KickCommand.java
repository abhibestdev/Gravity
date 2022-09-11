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
import us.blockgame.lib.util.MapUtil;
import us.blockgame.lib.util.StringUtil;
import us.blockgame.lib.util.ThreadUtil;

import java.util.Map;

public class KickCommand {

    @SneakyThrows
    @Command(name = "kick", permission = "gravity.command.kick", runAsync = true)
    public void kick(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <reason> <-s>");
            return;
        }
        Player target = LibPlugin.getPlayer(args.getArgs(0));
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        String reason = StringUtil.buildString(args.getArgs(), 1);
        boolean silent = reason.contains("-s");

        if (silent) reason = reason.replace("-s", "").replace("  ", " ").trim();

        if (reason.replace(" ", "").equalsIgnoreCase(""))
            reason = "Misconduct";

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

        //Get player's rank
        Rank playerRank = rankHandler.getOfflineRank(target.getUniqueId());

        Rank senderRank = Rank.OWNER;
        if (args.getSender() instanceof Player) {
            senderRank = rankHandler.getOfflineRank(args.getPlayer().getUniqueId());
        }

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", target.getUniqueId())).first();

        Map<String, Object> documentMap = MapUtil.cloneDocument(document);

        JSONObject jsonDocument = (JSONObject) new JSONParser().parse(document.toJson());
        JSONObject punishments = jsonDocument.containsKey("punishments") ? (JSONObject) jsonDocument.get("punishments") : new JSONObject();

        long kickTime = System.currentTimeMillis();

        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();

        //Create ban JSONObject
        JSONObject punishment = new JSONObject();
        punishment.put("reason", reason);
        punishment.put("type", "kick");
        punishment.put("uuid", cacheHandler.getUUID(args.getSender().getName()).toString());
        punishment.put("time", String.valueOf(kickTime));

        punishments.put(String.valueOf(kickTime), punishment);

        //Delete current document
        mongoCollection.deleteOne(document);

        documentMap.put("punishments", punishments);
        mongoCollection.insertOne(new Document(documentMap));

        PunishmentHandler punishmentHandler = GravityPlugin.getInstance().getPunishmentHandler();

        //Create this variable so it can be used in the lambda
        String finalReason = reason;

        ThreadUtil.runSync(() -> {
            target.kickPlayer(punishmentHandler.getFormattedKickReason(finalReason));
        });

        //Broadcast kick
        FancyMessage fancyBanMessage = new FancyMessage(silent ? "(Silent) " : "").color(ChatColor.GRAY).then(senderRank.getLitePrefix() + args.getSender().getName()).then(" has kicked ").color(ChatColor.GREEN).then(playerRank.getLitePrefix() + target.getName()).then(" for " + reason + ".").color(ChatColor.GREEN);
        Bukkit.getOnlinePlayers().stream().filter(p -> (silent ? p.hasPermission("gravity.silent") : true)).forEach(fancyBanMessage::send);
        return;
    }
}
