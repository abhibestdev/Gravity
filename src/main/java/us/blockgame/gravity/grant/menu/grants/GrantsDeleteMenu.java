package us.blockgame.gravity.grant.menu.grants;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.grant.Grant;
import us.blockgame.gravity.grant.menu.button.grants.delete.GrantsDeleteCancelButton;
import us.blockgame.gravity.grant.menu.button.grants.delete.GrantsDeleteConfirmationButton;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class GrantsDeleteMenu extends Menu {

    private UUID uuid;
    private Grant grant;

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Confirm Grant Deletion";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        //Set menu items
        buttonMap.put(3, new GrantsDeleteConfirmationButton(uuid, grant));
        buttonMap.put(5, new GrantsDeleteCancelButton());

        return buttonMap;
    }
}
