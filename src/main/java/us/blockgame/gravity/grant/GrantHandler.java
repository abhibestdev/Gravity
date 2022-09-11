package us.blockgame.gravity.grant;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.grant.command.GrantCommand;
import us.blockgame.gravity.grant.command.GrantsCommand;
import us.blockgame.gravity.grant.task.GrantTask;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class GrantHandler {

    public GrantHandler() {

        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new GrantCommand());
        commandHandler.registerCommand(new GrantsCommand());

        //Register task
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(GravityPlugin.getInstance(), new GrantTask(), 0L, 0L);
    }

    @SneakyThrows
    public List<Grant> getGrantsFromMongo(UUID uuid) {
        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        List<Grant> grantList = new ArrayList<>();

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        if (document != null && document.containsKey("grants")) {
            JSONObject jsonDocument = (JSONObject) new JSONParser().parse(document.toJson());

            JSONObject grants = (JSONObject) jsonDocument.get("grants");

            //Get grant data
            grants.keySet().forEach(key -> {

                JSONObject grant = (JSONObject) grants.get(key);

                long grantTime = Long.parseLong(key.toString());
                long duration = Long.parseLong((String) grant.get("duration"));
                String reason = (String) grant.get("reason");
                Rank rank = Rank.getRank((String) grant.get("rank"));
                UUID executor = UUID.fromString((String) grant.get("uuid"));

                grantList.add(new Grant(grantTime, duration, reason, rank, executor, rankHandler.getOfflineRank(executor)));
            });

            //Puts all grants in ascending order
            grantList.sort(Comparator.comparing(Grant::getTime));
        }
        return grantList;
    }
}
