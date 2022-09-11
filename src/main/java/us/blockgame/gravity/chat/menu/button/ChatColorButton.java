package us.blockgame.gravity.chat.menu.button;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.chat.ColorChat;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.util.CorrespondingWool;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.CC;
import us.blockgame.lib.util.ItemBuilder;

@AllArgsConstructor
public class ChatColorButton extends Button {

    private ColorChat colorChat;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .setName(colorChat.getChatColor() + colorChat.getName())
                .setLore(
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------",
                        ChatColor.BLUE + "Set your color chat to " + colorChat.getChatColor() + colorChat.getName() + ChatColor.BLUE + ".",
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------------")
                .setDurability((short) CorrespondingWool.getByColor(colorChat.getChatColor()).getId())
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Close menu
        player.closeInventory();

        //Set colored chat
        gravityProfile.setColorChat(colorChat);
        player.sendMessage(CC.YELLOW + "Your chat color has been set to " + colorChat.getChatColor() + colorChat.getName() + CC.PRIMARY + ".");
    }
}
