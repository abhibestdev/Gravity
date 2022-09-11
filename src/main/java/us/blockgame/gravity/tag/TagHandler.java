package us.blockgame.gravity.tag;

import org.bukkit.Bukkit;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.tag.impl.TagTask;
import us.blockgame.lib.util.Logger;

public class TagHandler {

    public TagHandler() {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GravityPlugin.getInstance(), () -> {
            if (!GravityPlugin.isScoreboardOverride()) {

                //Register tasks
                new TagTask().runTaskTimerAsynchronously(GravityPlugin.getInstance(), 1L, 1L);

                Logger.info("No scoreboard override was set. Using Gravity tags.");
            }
        }, 20L);
    }
}
