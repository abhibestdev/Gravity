package us.blockgame.gravity.mask.menu.button;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.util.ColorUtil;
import us.blockgame.gravity.util.CorrespondingWool;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;

@AllArgsConstructor
public class MaskRankButton extends Button {

    private Rank rank;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .setName(rank.getLitePrefix() + rank.getDisplayName())
                .setLore(
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------",
                        ChatColor.BLUE + "Mask as the " + rank.getLitePrefix() + rank.getDisplayName() + ChatColor.BLUE + " rank.",
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------")
                .setDurability((short) CorrespondingWool.getByColor(ColorUtil.getChatColor(rank.getLitePrefix())).getId())
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Close menu
        player.closeInventory();

        //Set the player's visible rank
        gravityProfile.setVisibleRank(rank);

        player.sendMessage(ChatColor.YELLOW + "You have masked as the " + rank.getLitePrefix() + rank.getName() + ChatColor.YELLOW + " rank.");
        return;
    }
}
