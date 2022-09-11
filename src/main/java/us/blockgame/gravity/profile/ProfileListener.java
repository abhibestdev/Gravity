package us.blockgame.gravity.profile;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.blockgame.gravity.GravityPlugin;

public class ProfileListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        //Create profile for player
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        if (!profileHandler.hasProfile(player)) {
            profileHandler.addPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        //Delete player's profile
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        profileHandler.removePlayer(player);
    }
}
