package us.blockgame.gravity.grant.task;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.grant.Grant;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.player.PlayerHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.util.MapUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class GrantTask extends BukkitRunnable {

    public void run() {
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        PlayerHandler playerHandler = GravityPlugin.getInstance().getPlayerHandler();

        Bukkit.getOnlinePlayers().stream().filter(p -> profileHandler.hasProfile(p)).forEach(p -> {
            GravityProfile gravityProfile = profileHandler.getGravityProfile(p);

            Grant grant = getLatestGrant(gravityProfile.getGrantList());

            //Set players rank to default if not active grant was found
            if (grant == null && gravityProfile.getRank() != Rank.DEFAULT) {
                gravityProfile.setRank(Rank.DEFAULT);
                playerHandler.updatePermissions(p);

                CompletableFuture.runAsync(() -> updateMongoRank(p.getUniqueId(), gravityProfile.getRank()));
            } else

                //Check if their rank is not the same as their latest grant
                if (grant != null && grant.getRank() != gravityProfile.getRank()) {
                    gravityProfile.setRank(grant.getRank());
                    playerHandler.updatePermissions(p);

                    CompletableFuture.runAsync(() -> updateMongoRank(p.getUniqueId(), gravityProfile.getRank()));
                }
        });
    }

    //Method to get last active grant on player
    public Grant getLatestGrant(List<Grant> grantList) {
        List<Grant> grants = new ArrayList<>(grantList);
        Collections.reverse(grants);

        return grants.stream().filter(g -> g.getDuration() == -1L || System.currentTimeMillis() <= g.getTime() + g.getDuration()).findFirst().orElse(null);
    }

    public void updateMongoRank(UUID uuid, Rank rank) {
        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

        //Check if the player's document exists
        if (document == null) {
            return;
        }

        Map<String, Object> documentMap = MapUtil.cloneDocument(document);

        //Delete current document
        mongoCollection.deleteOne(document);
        documentMap.put("rank", rank.getName());

        //Input new document into mongo
        mongoCollection.insertOne(new Document(documentMap));
    }
}
