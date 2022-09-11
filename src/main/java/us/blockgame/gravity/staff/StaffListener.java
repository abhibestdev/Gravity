package us.blockgame.gravity.staff;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.gravity.settings.SettingsHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.redis.RedisHandler;
import us.blockgame.lib.redis.event.RedisReceieveEvent;
import us.blockgame.lib.util.CC;
import us.blockgame.lib.util.MapUtil;

import java.util.Map;

public class StaffListener implements Listener {

  /*  @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        SettingsHandler settingsHandler = GravityPlugin.getInstance().getSettingsHandler();
        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();
        StaffHandler staffHandler = GravityPlugin.getInstance().getStaffHandler();

        //Send join message to redis
        Bukkit.getScheduler().runTaskLaterAsynchronously(GravityPlugin.getInstance(), () -> {
            if (player.hasPermission("core.staff") && System.currentTimeMillis() - staffHandler.getPingBack().getOrDefault(player.getName(), 0L) >= 1500L) {

                redisHandler.send(MapUtil.createMap(
                        "type", "join",
                        "server", settingsHandler.getServerName(),
                        "player", player.getName(),
                        "rank", rankHandler.getOfflineVisibleRank(player.getUniqueId()).getName()));
            }
        }, 30L);
    } */

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        StaffHandler staffHandler = GravityPlugin.getInstance().getStaffHandler();
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();

        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player is frozen
        if (player.hasMetadata("frozen")) {
            staffHandler.staffBroadcast(CC.AQUA + "[Staff] " + gravityProfile.getNameWithLitePrefix() + CC.BLUE + " has logged out while " + CC.RED + CC.BOLD + "FROZEN" + CC.BLUE + ".");
            return;
        }

        SettingsHandler settingsHandler = GravityPlugin.getInstance().getSettingsHandler();
        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

   /*     Bukkit.getScheduler().runTaskLaterAsynchronously(GravityPlugin.getInstance(), () -> {
            redisHandler.send(MapUtil.createMap(
                    "type", "staff-previous",
                    "server", settingsHandler.getServerName(),
                    "player", player.getName(),
                    "rank", rankHandler.getOfflineVisibleRank(player.getUniqueId()).getName()));
        }, 10L);

        Bukkit.getScheduler().runTaskLaterAsynchronously(GravityPlugin.getInstance(), () -> {
            if (System.currentTimeMillis() - staffHandler.getPingBack().getOrDefault(player.getName(), 0L) >= 1500L) {

                redisHandler.send(MapUtil.createMap(
                        "type", "quit",
                        "server", settingsHandler.getServerName(),
                        "player", player.getName(),
                        "rank", rankHandler.getOfflineVisibleRank(player.getUniqueId()).getName()));
            }
        }, 30L); */

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        //Ignore if player isn't frozen
        if (!player.hasMetadata("frozen")) return;

        //Check if player has moved
        if ((int) from.getX() != (int) to.getX() || (int) from.getZ() != (int) to.getZ()) {
            //Cancel movement
            event.setTo(from.setDirection(to.getDirection()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();

        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player is in staff chat
        if (gravityProfile.isStaffChat() && player.hasPermission("gravity.staff")) {
            event.setCancelled(true);

            StaffHandler staffHandler = GravityPlugin.getInstance().getStaffHandler();
            SettingsHandler settingsHandler = GravityPlugin.getInstance().getSettingsHandler();

            //Send staff chat to redis
            staffHandler.sendStaffChat(player.getName(), gravityProfile.getUsedRank(), event.getMessage(), settingsHandler.getServerName());
            return;
        }
    }

    @EventHandler
    public void onRedis(RedisReceieveEvent event) {
        Map<String, Object> messageMap = event.getMessageMap();

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        SettingsHandler settingsHandler = GravityPlugin.getInstance().getSettingsHandler();
        StaffHandler staffHandler = GravityPlugin.getInstance().getStaffHandler();
        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

        String type = (String) messageMap.get("type");

        switch (type) {
            case "join": {
                String server = (String) messageMap.get("server");
                String playerName = (String) messageMap.get("player");
                Rank rank = Rank.getRank((String) messageMap.get("rank"));

                staffHandler.staffBroadcast(CC.AQUA + "[Staff] " + rank.getLitePrefix() + playerName + CC.BLUE + " has joined " + CC.AQUA + server + CC.BLUE + ".");
                break;
            }
            case "quit": {
                String server = (String) messageMap.get("server");
                String playerName = (String) messageMap.get("player");
                Rank rank = Rank.getRank((String) messageMap.get("rank"));

                staffHandler.staffBroadcast(CC.AQUA + "[Staff] " + rank.getLitePrefix() + playerName + CC.BLUE + " has left " + CC.AQUA + server + CC.BLUE + ".");
                break;
            }
            case "switch": {
                String server = (String) messageMap.get("server");
                String playerName = (String) messageMap.get("player");
                Rank rank = Rank.getRank((String) messageMap.get("rank"));
                String to = (String) messageMap.get("to");

                staffHandler.staffBroadcast(CC.AQUA + "[Staff] " + rank.getLitePrefix() + playerName + CC.BLUE + " has joined " + CC.AQUA + to + CC.BLUE + " from " + CC.AQUA + server + CC.BLUE + ".");
                break;
            }
            case "staff-previous": {
                String server = (String) messageMap.get("server");
                String playerName = (String) messageMap.get("player");
                Rank rank = Rank.getRank((String) messageMap.get("rank"));

                Player target = Bukkit.getPlayer(playerName);

                //Check if player is online
                if (target != null) {
                    //Send ping back
                    redisHandler.send(MapUtil.createMap(
                            "type", "staff-ping-back",
                            "server", server,
                            "player", target.getName(),
                            "rank", rank.getName(),
                            "to", settingsHandler.getServerName()));
                }
                break;
            }
            case "staff-ping-back": {
                String server = (String) messageMap.get("server");

                if (server.equalsIgnoreCase(settingsHandler.getServerName())) {

                    String playerName = (String) messageMap.get("player");
                    Rank rank = Rank.getRank((String) messageMap.get("rank"));
                    String to = (String) messageMap.get("to");

                    redisHandler.send(MapUtil.createMap(
                            "type", "switch",
                            "server", settingsHandler.getServerName(),
                            "player", playerName,
                            "rank", rank.getName(),
                            "to", to));

                    staffHandler.getPingBack().put(playerName, System.currentTimeMillis());
                }
                break;
            }
            case "staff-chat": {
                String server = (String) messageMap.get("server");
                String playerName = (String) messageMap.get("player");
                Rank rank = Rank.getRank((String) messageMap.get("rank"));
                String message = (String) messageMap.get("message");

                staffHandler.staffBroadcast(CC.AQUA + "[SC] " + CC.GRAY + "[" + server + "] " + rank.getLitePrefix() + playerName + CC.RESET + ": " + CC.AQUA + message);
                break;
            }
            case "report": {
                String server = (String) messageMap.get("server");
                String playerName = (String) messageMap.get("player");
                Rank rank = Rank.getRank((String) messageMap.get("rank"));
                String targetName = (String) messageMap.get("target");
                Rank targetRank = Rank.getRank((String) messageMap.get("targetRank"));
                String reason = (String) messageMap.get("reason");

                staffHandler.staffBroadcast(CC.AQUA + "[Report] " + CC.GRAY + "[" + server + "] " + rank.getLitePrefix() + playerName + CC.BLUE + " has reported " + targetRank.getLitePrefix() + targetName + CC.BLUE + " for " + CC.AQUA + reason + CC.BLUE + ".");
                break;
            }
            case "request": {
                String server = (String) messageMap.get("server");
                String playerName = (String) messageMap.get("player");
                Rank rank = Rank.getRank((String) messageMap.get("rank"));
                String reason = (String) messageMap.get("reason");

                staffHandler.staffBroadcast(CC.AQUA + "[Request] " + CC.GRAY + "[" + server + "] " + rank.getLitePrefix() + playerName + CC.BLUE + " has requested assistance: " + CC.AQUA + reason + CC.BLUE);
                break;
            }
        }
    }
}
