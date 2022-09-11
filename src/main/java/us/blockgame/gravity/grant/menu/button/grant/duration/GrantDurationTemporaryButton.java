package us.blockgame.gravity.grant.menu.button.grant.duration;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.grant.menu.grant.GrantReasonMenu;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.lib.util.TimeUtil;

import java.util.UUID;

@AllArgsConstructor
public class GrantDurationTemporaryButton extends Button {

    private UUID uuid;
    private Rank rank;
    private Rank playerRank;

    @Override
    public ItemStack getButtonItem(Player player) {

        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        String username = cacheHandler.getUsername(uuid);

        return new ItemBuilder(Material.PAPER)
                .setName(ChatColor.YELLOW + "Temporary Grant")
                .setLore(ChatColor.GRAY + "Click to grant " + username + " the " + rank.getDisplayName() + " rank temporarily.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();
        player.beginConversation(new ConversationFactory(GravityPlugin.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {

                CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
                String username = cacheHandler.getUsername(uuid);

                return ChatColor.YELLOW + "Please enter how long you would like to grant " + playerRank.getLitePrefix() + username + ChatColor.YELLOW + " the " + rank.getLitePrefix() + rank.getDisplayName() + ChatColor.YELLOW + " for...";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String sDuration) {
                long duration = TimeUtil.parseTime(sDuration);

                GrantReasonMenu grantReasonMenu = new GrantReasonMenu(uuid, rank, duration, playerRank);
                grantReasonMenu.openMenu(player);
                return null;
            }

        }).withLocalEcho(false).buildConversation(player));
    }
}
