package us.blockgame.gravity.grant.menu.button.grant.reason;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.grant.menu.grant.GrantConfirmationMenu;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;

import java.util.UUID;

@AllArgsConstructor
public class GrantReasonNoReasonButton extends Button {

    private UUID uuid;
    private Rank rank;
    private long duration;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.REDSTONE).setName(ChatColor.RED + "No Reason").setLore(ChatColor.GRAY + "Don't provide a reason for this grant.").toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        GrantConfirmationMenu grantConfirmationMenu = new GrantConfirmationMenu(uuid, rank, "No Reason", duration);
        grantConfirmationMenu.openMenu(player);
        return;
    }
}
