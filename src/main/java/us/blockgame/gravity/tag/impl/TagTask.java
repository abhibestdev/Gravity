package us.blockgame.gravity.tag.impl;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.nametag.NametagHandler;

public class TagTask extends BukkitRunnable {

    public void run() {
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        NametagHandler nametagHandler = LibPlugin.getInstance().getNametagHandler();

        Bukkit.getOnlinePlayers().forEach(updateFor -> {

            //Get all players with an existing profile
            Bukkit.getOnlinePlayers().stream().filter(player -> profileHandler.hasProfile(player)).forEach(player -> {
                GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

                Rank rank = gravityProfile.getUsedRank();

                //Update prefix
                nametagHandler.setPrefix(updateFor, player, rank.getLitePrefix());
            });

        });
    }


}
