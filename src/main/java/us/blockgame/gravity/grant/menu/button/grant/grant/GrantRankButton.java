package us.blockgame.gravity.grant.menu.button.grant.grant;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.grant.menu.grant.GrantDurationMenu;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.gravity.util.ColorUtil;
import us.blockgame.gravity.util.CorrespondingWool;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;

import java.util.UUID;

@RequiredArgsConstructor
public class GrantRankButton extends Button {

    private final UUID uuid;
    private final Rank rank;

    private Rank playerRank;

    @Override
    public ItemStack getButtonItem(Player player) {

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        playerRank = rankHandler.getOfflineRank(uuid);

        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        String username = cacheHandler.getUsername(uuid);

        return new ItemBuilder(Material.WOOL)
                .setName(rank.getLitePrefix() + rank.getDisplayName())
                .setLore(
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------",
                        ChatColor.BLUE + "Grant " + playerRank.getLitePrefix() + username + ChatColor.BLUE + " the " + rank.getLitePrefix() + rank.getDisplayName() + ChatColor.BLUE + " rank.",
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------")
                .setDurability((short) CorrespondingWool.getByColor(ColorUtil.getChatColor(rank.getLitePrefix())).getId())
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        GrantDurationMenu grantDurationMenu = new GrantDurationMenu(uuid, rank, playerRank);
        grantDurationMenu.openMenu(player);
    }
}
