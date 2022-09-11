package us.blockgame.gravity.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.grant.GrantHandler;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.settings.SettingsHandler;
import us.blockgame.lib.util.MapUtil;
import us.blockgame.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {

    private List<UUID> loginFromNewIp;

    public PlayerListener() {
        //Set list to empty list
        loginFromNewIp = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrelogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        SettingsHandler settingsHandler = GravityPlugin.getInstance().getSettingsHandler();

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        String ip = event.getAddress().getHostAddress();

        //If player document was not found, make one
        Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();
        if (document == null) {

            Map<String, Object> documentMap = MapUtil.createMap(
                    "_id", uuid,
                    "rank", Rank.DEFAULT.getName()
            );
            if (settingsHandler.isLoginStats()) {
                documentMap.put("firstLogin", System.currentTimeMillis());
                documentMap.put("lastLogin", System.currentTimeMillis());
                documentMap.put("lastLogout", 0L);
            }
            List<String> ips = new ArrayList<>();
            ips.add(ip);
            documentMap.put("ips", ips);

            mongoCollection.insertOne(new Document(documentMap));
        } else {
            Map<String, Object> documentMap = MapUtil.cloneDocument(document);

            List<String> ips = (List<String>) documentMap.get("ips");
            if (!ips.contains(ip)) {

                //Player logged in from new IP
                loginFromNewIp.add(uuid);
            }

            if (settingsHandler.isLoginStats()) {
                //Set last login to now
                documentMap.put("lastLogin", System.currentTimeMillis());


                //Delete original document
                mongoCollection.deleteOne((Document) mongoCollection.find(Filters.eq("_id", uuid)).first());

                //Insert mongo document
                mongoCollection.insertOne(new Document(documentMap));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("players");

        GrantHandler grantHandler = GravityPlugin.getInstance().getGrantHandler();

        PlayerHandler playerHandler = GravityPlugin.getInstance().getPlayerHandler();

        //Create permission attachment for player
        gravityProfile.setPermissionAttachment(player.addAttachment(GravityPlugin.getInstance()));

        CompletableFuture.runAsync(() -> {
            //If player document was not found, make one
            Document document = (Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first();
            if (document != null) {

                //Set rank of player
                gravityProfile.setRank(Rank.getRank(document.getString("rank")));

                //Set grants list
                gravityProfile.setGrantList(grantHandler.getGrantsFromMongo(player.getUniqueId()));

                //Set ip list
                gravityProfile.setIps((List<String>) document.get("ips"));

                //Check if player has 2fa enabled
                if (document.containsKey("gauth")) {
                    gravityProfile.setGAuthEnabled(document.getBoolean("gauth-enabled"));
                    gravityProfile.setGAuthKey(document.getString("gauth"));
                }
                //Check if player has ignored players
                if (document.containsKey("ignored")) {
                    gravityProfile.setIgnored((List<UUID>) document.get("ignored"));
                }
            }
            //Save player's login time
            gravityProfile.setLoginTime(System.currentTimeMillis());

            //Set player's login from new ip
            gravityProfile.setLoginFromNewIp(loginFromNewIp.contains(player.getUniqueId()));

            //Remove player from list
            loginFromNewIp.remove(player.getUniqueId());

            //Set player's permissions
            playerHandler.setPermissions(player);
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        SettingsHandler settingsHandler = GravityPlugin.getInstance().getSettingsHandler();
        PlayerHandler playerHandler = GravityPlugin.getInstance().getPlayerHandler();

        CompletableFuture.runAsync(() -> {
            MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("players");

            //If player document was not found, make one
            Document document = (Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first();

            if (settingsHandler.isLogoutStats()) {
                Map<String, Object> documentMap = MapUtil.cloneDocument(document);

                documentMap.put("lastLogout", System.currentTimeMillis());

                //Delete original document
                mongoCollection.deleteOne(document);

                //Insert mongo document
                mongoCollection.insertOne(new Document(documentMap));
            }
        });
        //Unset their permission
        playerHandler.unsetPermissions(player);
    }
}
