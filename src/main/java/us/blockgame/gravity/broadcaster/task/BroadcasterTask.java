package us.blockgame.gravity.broadcaster.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.broadcaster.BroadcasterHandler;
import us.blockgame.lib.util.CC;

public class BroadcasterTask extends BukkitRunnable {

    private int i = 0;

    @Override
    public void run() {
        BroadcasterHandler broadcasterHandler = GravityPlugin.getInstance().getBroadcasterHandler();

        //Reset index
        if (i >= broadcasterHandler.getBroadcasts().size()) {
            i = 0;
        }

        //Broadcast message
        String message = broadcasterHandler.getBroadcasts().get(i);
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(CC.translate(message));
        Bukkit.broadcastMessage(" ");

        //Move indexes
        i += 1;
    }
}
