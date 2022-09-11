package us.blockgame.gravity.grant.menu.grant;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.grant.menu.button.grant.grant.GrantRankButton;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class GrantMenu extends Menu {

    private UUID uuid;

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a rank to grant";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        for (int i = 0; i < Rank.values().length; i++) {
            Rank rank = Rank.values()[i];

            buttonMap.put(i, new GrantRankButton(uuid, rank));
        }

        return buttonMap;
    }
}
