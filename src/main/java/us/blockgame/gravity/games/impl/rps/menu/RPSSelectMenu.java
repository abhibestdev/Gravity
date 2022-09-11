package us.blockgame.gravity.games.impl.rps.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.games.Game;
import us.blockgame.gravity.games.impl.rps.RPS;
import us.blockgame.gravity.games.impl.rps.RPSType;
import us.blockgame.gravity.games.impl.rps.menu.button.RPSTypeButton;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class RPSSelectMenu extends Menu {

    private Game game;
    private RPS rps;
    private boolean one;

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a type";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        AtomicInteger atomicSlot = new AtomicInteger(2);

        //Set rock, papers, and scissors button
        Arrays.stream(RPSType.values()).forEach(rpsType -> {
            buttonMap.put(atomicSlot.addAndGet(1), new RPSTypeButton(game, rps, one, rpsType));
        });

        return buttonMap;
    }

    @Override
    public void onClose(Player player) {
        if ((one && rps.getP1Type() == null) || (!one && rps.getP2Type() == null)) {
            //Cancel game if player closes menu without selecting
            game.end(-1);
            return;
        }
    }
}
