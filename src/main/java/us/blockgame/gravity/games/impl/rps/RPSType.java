package us.blockgame.gravity.games.impl.rps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.util.ItemBuilder;

@AllArgsConstructor
public enum RPSType {

    ROCK("Rock", new ItemBuilder(Material.COBBLESTONE).toItemStack()),
    PAPER("Paper", new ItemBuilder(Material.PAPER).toItemStack()),
    SCISSORS("Scissors", new ItemBuilder(Material.SHEARS).toItemStack());

    @Getter private String name;
    @Getter private ItemStack item;
}
