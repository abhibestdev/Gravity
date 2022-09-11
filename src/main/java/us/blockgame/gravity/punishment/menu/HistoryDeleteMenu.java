package us.blockgame.gravity.punishment.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import us.blockgame.gravity.punishment.menu.button.delete.HistoryDeleteCancelButton;
import us.blockgame.gravity.punishment.menu.button.delete.HistoryDeleteConfirmationButton;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class HistoryDeleteMenu extends Menu {

    private UUID uuid;
    private JSONObject punishmentObject;

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Confirm Punishment Deletion";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        //Set menu items
        buttonMap.put(3, new HistoryDeleteConfirmationButton(uuid, punishmentObject));
        buttonMap.put(5, new HistoryDeleteCancelButton());

        return buttonMap;
    }
}