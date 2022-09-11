package us.blockgame.gravity.punishment;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONObject;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.punishment.impl.MutePunishment;
import us.blockgame.lib.redis.event.RedisReceieveEvent;
import us.blockgame.lib.util.ThreadUtil;

import java.util.Map;

public class PunishmentListener implements Listener {

    @SneakyThrows
    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        PunishmentHandler punishmentHandler = GravityPlugin.getInstance().getPunishmentHandler();

        JSONObject activeBan = punishmentHandler.getActiveBanOrBlacklist(event.getUniqueId());

        //Check if player is banned
        if (activeBan != null) {
            boolean ban = ((String) activeBan.get("type")).equals("ban");
            String reason = (String) activeBan.get("reason");
            long duration = Long.parseLong((String) activeBan.get("duration"));
            boolean perm = duration == -1L;
            long time = Long.parseLong((String) activeBan.get("time"));

            //Don't allow them to join
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, punishmentHandler.getFormattedBanReason(reason, time, duration, ban));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PunishmentHandler punishmentHandler = GravityPlugin.getInstance().getPunishmentHandler();

        JSONObject activeMute = punishmentHandler.getActiveMute(player.getUniqueId());

        //Check if player is muted
        if (activeMute != null) {
            String reason = (String) activeMute.get("reason");
            long duration = Long.parseLong((String) activeMute.get("duration"));
            boolean perm = duration == -1L;
            long time = Long.parseLong((String) activeMute.get("time"));

            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
            GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

            //Set player's mute
            gravityProfile.setMutePunishment(new MutePunishment(reason, time, duration));
        }
    }

    @EventHandler
    public void onRedis(RedisReceieveEvent event) {
        Map<String, Object> messageMap = event.getMessageMap();

        PunishmentHandler punishmentHandler = GravityPlugin.getInstance().getPunishmentHandler();

        String type = (String) messageMap.get("type");
        switch (type) {
            case "ban": {
                String playerName = (String) messageMap.get("player");
                String reason = (String) messageMap.get("reason");
                long duration = Long.parseLong((String) messageMap.get("duration"));
                long banTime = Long.parseLong((String) messageMap.get("time"));

                Player target = Bukkit.getPlayer(playerName);

                //Make sure player is online
                if (target != null) {
                    //Kick the player
                    ThreadUtil.runSync(() -> {
                        target.kickPlayer(punishmentHandler.getFormattedBanReason(reason, banTime, duration, true));
                    });
                }
                break;
            }
            case "mute": {
                String playerName = (String) messageMap.get("player");
                String reason = (String) messageMap.get("reason");
                long duration = Long.parseLong((String) messageMap.get("duration"));
                long muteTime = Long.parseLong((String) messageMap.get("time"));

                Player target = Bukkit.getPlayer(playerName);

                //Make sure player is online
                if (target != null) {
                    ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
                    GravityProfile gravityProfile = profileHandler.getGravityProfile(target);

                    //Set player's mute
                    gravityProfile.setMutePunishment(new MutePunishment(reason, muteTime, duration));
                }
                break;
            }
            case "unmute": {
                String playerName = (String) messageMap.get("player");

                Player target = Bukkit.getPlayer(playerName);

                //Make sure player is online
                if (target != null) {
                    ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
                    GravityProfile gravityProfile = profileHandler.getGravityProfile(target);

                    //Remove player's mute
                    gravityProfile.setMutePunishment(null);
                    break;
                }
            }
            case "blacklist" : {
                String playerName = (String) messageMap.get("player");
                String reason = (String) messageMap.get("reason");
                String ip = (String) messageMap.get("ip");

                ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
                Bukkit.getOnlinePlayers().forEach(p -> {
                    GravityProfile gravityProfile = profileHandler.getGravityProfile(p);

                    if (gravityProfile.getIps().contains(ip)) {

                        //Kick everyone who has matching ips
                        ThreadUtil.runSync(() -> {
                            p.kickPlayer(punishmentHandler.getFormattedBanReason(reason, 0L, 0L, false));
                        });
                    }
                });
                break;
            }
        }
    }
}
