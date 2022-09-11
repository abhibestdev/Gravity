package us.blockgame.gravity.grant.menu.button.grants.grants;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.grant.Grant;
import us.blockgame.gravity.grant.menu.grants.GrantsDeleteMenu;
import us.blockgame.gravity.util.ColorUtil;
import us.blockgame.gravity.util.CorrespondingWool;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.DateUtil;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.lib.util.TimeUtil;

import java.util.UUID;

@AllArgsConstructor
public class GrantsGrantButton extends Button {

    private UUID uuid;
    private Grant grant;

    @Override
    public ItemStack getButtonItem(Player player) {

        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        return new ItemBuilder(Material.WOOL)
                .setDurability((short) CorrespondingWool.getByColor(ColorUtil.getChatColor(grant.getRank().getLitePrefix())).getId())
                .setName(grant.getRank().getLitePrefix() + grant.getRank().getName() + ChatColor.GRAY + " ┃ " + ChatColor.WHITE + DateUtil.millisToDate(grant.getTime()))
                .setLore(
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------",
                        ChatColor.BLUE + "Granted By: " + grant.getExecutorRank().getLitePrefix() + cacheHandler.getUsername(grant.getUuid()),
                        ChatColor.BLUE + "Reason: " + ChatColor.WHITE + grant.getReason(),
                        ChatColor.BLUE + "Duration: " + ChatColor.WHITE + (grant.getDuration() == -1 ? "Forever" : TimeUtil.formatTimeMillis(grant.getDuration(), false, true) + (grant.getTime() + grant.getDuration() < System.currentTimeMillis() && grant.getDuration() != -1L ? ChatColor.GRAY + " (Expired)" : "")),
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------",
                        ChatColor.RED.toString() + ChatColor.BOLD + "RIGHT CLICK TO DELETE THIS GRANT",
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------"
                ).toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (!clickType.isRightClick()) return;

        GrantsDeleteMenu grantsDeleteMenu = new GrantsDeleteMenu(uuid, grant);
        grantsDeleteMenu.openMenu(player);
    }
}
