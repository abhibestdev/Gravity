package us.blockgame.gravity.grant.menu.button.grants.delete;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;

public class GrantsDeleteCancelButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .setDurability((short) 14)
                .setName(ChatColor.RED + "Cancel Grant Deletion")
                .setLore(ChatColor.GRAY + "Cancel the grant deletion process.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();

        player.sendMessage(ChatColor.YELLOW + "You have canceled the grant deletion process.");
        return;
    }
}
