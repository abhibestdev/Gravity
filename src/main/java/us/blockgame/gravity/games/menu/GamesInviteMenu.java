package us.blockgame.gravity.games.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.games.GameType;
import us.blockgame.gravity.games.menu.button.GamesGameButton;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class GamesInviteMenu extends Menu {

    private UUID uuid;

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a game";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        AtomicInteger atomicSlot = new AtomicInteger(0);

        //Add buttons
        Arrays.stream(GameType.values()).forEach(gameType -> buttonMap.put(atomicSlot.getAndAdd(1), new GamesGameButton(gameType, uuid)));
        return buttonMap;
    }
}
