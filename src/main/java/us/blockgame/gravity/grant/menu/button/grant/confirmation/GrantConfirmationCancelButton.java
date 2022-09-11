package us.blockgame.gravity.grant.menu.button.grant.confirmation;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;

public class GrantConfirmationCancelButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .setDurability((short) 14)
                .setName(ChatColor.RED + "Cancel Grant")
                .setLore(ChatColor.GRAY + "Cancel the grant process.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();

        player.sendMessage(ChatColor.YELLOW + "You have canceled the granting process.");
        return;
    }
}
