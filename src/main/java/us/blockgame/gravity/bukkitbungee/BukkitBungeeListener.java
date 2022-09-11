package us.blockgame.gravity.bukkitbungee;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.redis.RedisHandler;
import us.blockgame.lib.util.MapUtil;

public class BukkitBungeeListener implements Listener, PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        String message = new String(bytes);

        //Check if message is from gravity
        if (channel.equalsIgnoreCase("msg:gravity")) {

            String[] data = message.split(":");

            String type = data[0];

            RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();
            RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();

            GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

            String server = data[1];

            Rank rank = rankHandler.getOfflineRank(player.getUniqueId());

            //Ignore if rank is not a staff rank
            if (!rank.isStaff()) return;

            //Check message type
            switch (type) {
                case "join": {

                    redisHandler.send(MapUtil.createMap(
                            "type", "join",
                            "server", server,
                            "player", player.getName(),
                            "rank", rank.getName()));
                    break;
                }
                case "switch": {
                    String to = data[2];

                    redisHandler.send(MapUtil.createMap(
                            "type", "switch",
                            "server", server,
                            "player", player.getName(),
                            "rank", rank.getName(),
                            "to", to));
                    break;
                }
                case "quit": {
                    redisHandler.send(MapUtil.createMap(
                            "type", "quit",
                            "server", server,
                            "player", player.getName(),
                            "rank", rank.getName()));
                    break;
                }
            }
        }
    }
}
