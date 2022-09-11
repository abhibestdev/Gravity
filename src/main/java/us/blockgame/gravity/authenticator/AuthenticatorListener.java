package us.blockgame.gravity.authenticator;

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
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.lib.util.MapUtil;

import java.util.List;
import java.util.Map;

public class AuthenticatorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player logged in from new ip
        if (gravityProfile.isLoginFromNewIp() && gravityProfile.getGAuthKey() != null && gravityProfile.isGAuthEnabled()) {
            player.sendMessage(ChatColor.RED + "Please open the Google Authenticator app and provide the six digit passcode.\nThis passcode is required before continuing.");
        } else {
            //If player doesn't have 2fa, pass them
            gravityProfile.setPassed2fa(true);
        }

        new BukkitRunnable() {
            public void run() {
                //Ignore if player logged off already
                if (!player.isOnline()) return;

                if (player.hasPermission("gravity.staff") && gravityProfile.getGAuthKey() == null) {
                    GoogleAuthenticator googleAuthenticator = GravityPlugin.getGoogleAuthenticator();
                    GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();

                    String key = googleAuthenticatorKey.getKey();

                    player.sendMessage(ChatColor.RED + "You do not have 2FA enabled! Please enter this code in your Google Authenticator App before disconnecting.");
                    player.sendMessage(ChatColor.RED + "Code: " + key);

                    gravityProfile.setGAuthKey(key);

                    MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
                    MongoCollection mongoCollection = mongoHandler.getCollection("players");

                    Document document = (Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first();

                    Map<String, Object> documentMap = MapUtil.cloneDocument(document);
                    documentMap.put("gauth", key);
                    documentMap.put("gauth-enabled", true);

                    //Delete old document
                    mongoCollection.deleteOne(document);

                    //Insert new one
                    mongoCollection.insertOne(new Document(documentMap));
                }
            }
        }.runTaskLaterAsynchronously(GravityPlugin.getInstance(), 60L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player passed 2fa
        if (gravityProfile.isPassed2fa()) return;

        //Cancel chat
        event.setCancelled(true);

        try {
            int code = Integer.parseInt(event.getMessage());

            GoogleAuthenticator googleAuthenticator = GravityPlugin.getGoogleAuthenticator();

            if (!googleAuthenticator.authorize(gravityProfile.getGAuthKey(), code)) {
                player.sendMessage(ChatColor.RED + "The code you have entered is either expired or invalid. Please try again.");
                return;
            }
            gravityProfile.setPassed2fa(true);

            MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("players");
            Document document = (Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first();

            if (document != null) {
                Map<String, Object> documentMap = MapUtil.cloneDocument(document);

                List<String> ips = (List<String>) documentMap.get("ips");

                //Add ip to their list of ips because it has been authed
                ips.add(player.getAddress().getAddress().getHostAddress());

                //Delete old document
                mongoCollection.deleteOne((Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first());

                //Insert new document
                mongoCollection.insertOne(new Document(documentMap));
            }

            player.sendMessage(ChatColor.GREEN + "Thank you for authenticating!");
            return;
        } catch (Exception ex) {
            player.sendMessage(ChatColor.RED + "There was an error processing this entry. Please try again.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player passed 2fa
        if (gravityProfile.isPassed2fa()) return;

        //Cancel
        event.setCancelled(true);

        player.sendMessage(ChatColor.RED + "You must authenticate with 2FA before running commands.");
        return;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player passed 2fa
        if (gravityProfile.isPassed2fa()) return;

        //Cancel
        event.setCancelled(true);
        return;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player passed 2fa
        if (gravityProfile.isPassed2fa()) return;

        //Cancel
        event.setCancelled(true);
        return;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player passed 2fa
        if (gravityProfile.isPassed2fa()) return;

        //Cancel
        event.setCancelled(true);
        return;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player passed 2fa
        if (gravityProfile.isPassed2fa()) return;

        //Cancel
        event.setCancelled(true);
        return;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player passed 2fa
        if (gravityProfile.isPassed2fa()) return;

        //Cancel
        event.setCancelled(true);
        return;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player passed 2fa
        if (gravityProfile.isPassed2fa()) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        //Don't allow player to move if they are not authenticated
        if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
            event.setTo(from.setDirection(to.getDirection()));
            return;
        }
    }

}
