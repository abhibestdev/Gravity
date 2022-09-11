package us.blockgame.gravity.punishment.menu.button.delete;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;

public class HistoryDeleteCancelButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .setDurability((short) 4)
                .setName(ChatColor.RED + "Cancel Punishment Deletion")
                .setLore(ChatColor.GRAY + "Cancel the punishment deletion process.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();

        player.sendMessage(ChatColor.YELLOW + "You have canceled the punishment deletion process.");
        return;
    }
}
