package us.blockgame.gravity.rank;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.player.PlayerHandler;
import us.blockgame.lib.redis.event.RedisReceieveEvent;

import java.util.Map;

public class RankListener implements Listener {

    @EventHandler
    public void onRedis(RedisReceieveEvent event) {
        Map<String, Object> messageMap = event.getMessageMap();

        String type = (String) messageMap.get("type");

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        PlayerHandler playerHandler = GravityPlugin.getInstance().getPlayerHandler();

        switch (type) {
            case "rank-add-perm": {
                String rankName = (String) messageMap.get("rank");
                String permission = (String) messageMap.get("permission");

                Rank rank = Rank.getRank(rankName);

                //Add permission to rank
                if (!rank.getPermissions().contains(permission)) {
                    rank.getPermissions().add(permission);
                }

                //Update permissions
                rankHandler.affectedPlayersOfRankOrAboveAction(rank, p -> playerHandler.updatePermissions(p));
                break;
            }
            case "rank-del-perm": {
                String rankName = (String) messageMap.get("rank");
                String permission = (String) messageMap.get("permission");

                Rank rank = Rank.getRank(rankName);

                //Remove permission from rank
                if (rank.getPermissions().contains(permission)) {
                    rank.getPermissions().remove(permission);
                }
                //Update permissions
                rankHandler.affectedPlayersOfRankOrAboveAction(rank, p -> playerHandler.updatePermissions(p));
                break;
            }
        }
    }

}
