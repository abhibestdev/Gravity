package us.blockgame.gravity.broadcaster;

import lombok.Getter;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.broadcaster.task.BroadcasterTask;

import java.util.List;

public class BroadcasterHandler {

    @Getter private List<String> broadcasts;

    public BroadcasterHandler() {
        broadcasts = GravityPlugin.getInstance().getConfig().getStringList("broadcasts");

        new BroadcasterTask().runTaskTimerAsynchronously(GravityPlugin.getInstance(), 1200L, 6000L);
    }


}
