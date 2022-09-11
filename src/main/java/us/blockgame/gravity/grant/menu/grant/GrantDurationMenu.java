package us.blockgame.gravity.grant.menu.grant;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.grant.menu.button.grant.duration.GrantDurationPermanentButton;
import us.blockgame.gravity.grant.menu.button.grant.duration.GrantDurationTemporaryButton;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class GrantDurationMenu extends Menu {

    private UUID uuid;
    private Rank rank;
    private Rank playerRank;

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a grant duration";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        //Set items of the menu
        buttonMap.put(3, new GrantDurationPermanentButton(uuid, rank, playerRank));
        buttonMap.put(5, new GrantDurationTemporaryButton(uuid, rank, playerRank));

        return buttonMap;
    }
}
