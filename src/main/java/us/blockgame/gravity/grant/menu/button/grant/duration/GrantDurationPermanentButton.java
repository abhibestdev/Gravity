package us.blockgame.gravity.grant.menu.button.grant.duration;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.grant.menu.grant.GrantReasonMenu;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;

import java.util.UUID;

@AllArgsConstructor
public class GrantDurationPermanentButton extends Button {

    private UUID uuid;
    private Rank rank;
    private Rank playerRank;

    @Override
    public ItemStack getButtonItem(Player player) {

        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        String username = cacheHandler.getUsername(uuid);

        return new ItemBuilder(Material.DIAMOND)
                .setName(ChatColor.AQUA + "Permanent Grant")
                .setLore(ChatColor.GRAY + "Click to grant " + username + " the " + rank.getDisplayName() + " rank forever.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        GrantReasonMenu grantReasonMenu = new GrantReasonMenu(uuid, rank, -1L, playerRank);
        grantReasonMenu.openMenu(player);
    }
}
