package us.blockgame.gravity.mask.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.mask.menu.button.MaskRankButton;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MaskMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a rank";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        Rank rank = gravityProfile.getRank();

        AtomicInteger atomicSlot = new AtomicInteger();

        //Make button for every rank including their current rank and the rank under theirs
        Arrays.stream(Rank.values()).filter(r -> r.getWeight() > rank.getWeight()).forEach(r -> {
            buttonMap.put(atomicSlot.getAndAdd(1), new MaskRankButton(r));
        });
        return buttonMap;
    }
}
