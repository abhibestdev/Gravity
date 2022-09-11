package us.blockgame.gravity.grant.menu.grant;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.grant.menu.button.grant.confirmation.GrantConfirmationCancelButton;
import us.blockgame.gravity.grant.menu.button.grant.confirmation.GrantConfirmationConfirmButton;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class GrantConfirmationMenu extends Menu {

    private UUID uuid;
    private Rank rank;
    private String reason;
    private long duration;

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Confirm grant";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        //Set menu items
        buttonMap.put(3, new GrantConfirmationConfirmButton(uuid, rank, reason, duration));
        buttonMap.put(5, new GrantConfirmationCancelButton());

        return buttonMap;
    }
}
