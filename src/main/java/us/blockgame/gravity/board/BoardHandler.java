package us.blockgame.gravity.board;

import org.bukkit.Bukkit;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.scoreboard.ScoreboardHandler;
import us.blockgame.lib.util.Logger;

public class BoardHandler {

    public BoardHandler() {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GravityPlugin.getInstance(), () -> {
            if (!GravityPlugin.isScoreboardOverride()) {

                //Register scoreboard
                ScoreboardHandler scoreboardHandler = LibPlugin.getInstance().getScoreboardHandler();
                scoreboardHandler.setScoreboard(new GravityBoard());

                Logger.info("No scoreboard override was set. Using Gravity board.");
            }
        }, 20L);
    }
}
