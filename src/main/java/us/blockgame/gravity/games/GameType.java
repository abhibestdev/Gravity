package us.blockgame.gravity.games;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.util.ItemBuilder;

@AllArgsConstructor
public enum GameType {

    RPS("Rock Paper Scissors", new ItemBuilder(Material.PAPER).toItemStack());

    @Getter private String displayName;
    @Getter private ItemStack displayItem;
}
