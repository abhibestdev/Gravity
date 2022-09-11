package us.blockgame.gravity.tab;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.tab.BGTab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GravityTab implements BGTab {

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public String getFooter() {
        return null;
    }

    @Override
    public Map<Integer, String> getSlots(Player player) {
        Map<Integer, String> slots = Maps.newHashMap();

        List<GravityTabPlayer> gravityTabPlayerList = new ArrayList<>();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();

        //Add all players to gravityTabPlayerList and sort the list by rank
        Bukkit.getOnlinePlayers().stream().filter(p -> profileHandler.hasProfile(p)).forEach(p -> {
            GravityProfile gravityProfile = profileHandler.getGravityProfile(p);
            gravityTabPlayerList.add(new GravityTabPlayer(p.getName(), gravityProfile.getRank() == null ? Rank.DEFAULT : gravityProfile.getRank()));
        });
        gravityTabPlayerList.sort(Comparator.comparing(g -> g.getRank().getWeight()));

        //Take gravityTabPlayerList and format it into the tab we want
        int left = 0;
        int middle = 0;
        int right = 0;

        String lastColumn = "r";

        for (GravityTabPlayer gravityTabPlayer : gravityTabPlayerList) {
            String newColumn = "";

            int slot = 0;

            switch (lastColumn) {
                case "r": {
                    newColumn = "l";
                    left += 1;

                    slot = left;
                    break;
                }
                case "l": {
                    newColumn = "m";
                    middle += 1;

                    slot = 20 + middle;
                    break;
                }
                case "m": {
                    newColumn = "r";
                    right += 1;

                    slot = 40 + right;
                    break;
                }
            }
            slots.put(slot, gravityTabPlayer.getRank() == null ? "" : gravityTabPlayer.getRank().getLitePrefix() + gravityTabPlayer.getName());
            lastColumn = newColumn;
        }
        return slots;
    }
}
