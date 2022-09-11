package us.blockgame.gravity.profile;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;

import java.util.Map;
import java.util.UUID;

public class ProfileHandler {

    private Map<UUID, GravityProfile> gravityProfileMap;

    public ProfileHandler() {
        gravityProfileMap = Maps.newHashMap();

        //Register Listeners
        Bukkit.getPluginManager().registerEvents(new ProfileListener(), GravityPlugin.getInstance());
    }

    public void addPlayer(Player player) {
        gravityProfileMap.put(player.getUniqueId(), new GravityProfile(player.getUniqueId()));
    }

    public void removePlayer(Player player) {
        gravityProfileMap.remove(player.getUniqueId());
    }

    public boolean hasProfile(Player player) {
        return gravityProfileMap.containsKey(player.getUniqueId());
    }

    public GravityProfile getGravityProfile(Player player) {
        return gravityProfileMap.get(player.getUniqueId());
    }
}
