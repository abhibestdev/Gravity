package us.blockgame.gravity.games.impl.rps.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.games.Game;
import us.blockgame.gravity.games.impl.rps.RPS;
import us.blockgame.gravity.games.impl.rps.RPSType;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.CC;
import us.blockgame.lib.util.ItemBuilder;

@RequiredArgsConstructor
public class RPSTypeButton extends Button {

    private final Game game;
    private final RPS rps;
    private final boolean one;
    private final RPSType rpsType;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(rpsType.getItem().clone()).setName(CC.GOLD + rpsType.getName())
                .setLore(CC.GRAY + "Click to select " + rpsType.getName() + ".")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        //Save selection
        if (one)
            rps.setP1Type(rpsType);
        else
            rps.setP2Type(rpsType);

        //Close menu
        player.closeInventory();

        player.sendMessage(CC.YELLOW + "You have selected " + CC.GOLD + rpsType.getName() + CC.YELLOW + ".");

        //End game
        if (rps.getP1Type() != null && rps.getP2Type() != null)
            game.end(rps.compare());
        return;
    }

}
