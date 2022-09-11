package us.blockgame.gravity.games.menu.button;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.games.GameInvite;
import us.blockgame.gravity.games.GameType;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.lib.fanciful.FancyMessage;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.CC;
import us.blockgame.lib.util.ItemBuilder;

import java.util.UUID;

@AllArgsConstructor
public class GamesGameButton extends Button {

    private GameType gameType;
    private UUID uuid;

    @Override
    public ItemStack getButtonItem(Player player) {
        Player target = Bukkit.getPlayer(uuid);

        return new ItemBuilder(gameType.getDisplayItem().clone())
                .setName(CC.GOLD + gameType.getDisplayName())
                .setLore(ChatColor.GRAY + "Click to send " + target.getName() + "'s a " + gameType.getDisplayName() + " invite.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Close menu
        player.closeInventory();

        Player target = Bukkit.getPlayer(uuid);

        //Make sure player is online
        if (target == null) {
            player.sendMessage(CC.RED + "That player is no longer online.");
            return;
        }
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile targetProfile = profileHandler.getGravityProfile(target);

        //Make sure target is not in a game
        if (targetProfile.isPlayingGame()) {
            player.sendMessage(ChatColor.RED + "That player is already in a game.");
            return;
        }
        targetProfile.getGameInviteList().add(new GameInvite(player.getUniqueId(), gameType));

        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);
        player.sendMessage(ChatColor.YELLOW + "You have sent " + targetProfile.getNameWithLitePrefix() + ChatColor.YELLOW + " a " + ChatColor.GOLD + gameType.getDisplayName() + ChatColor.YELLOW + " invite.");

        FancyMessage fancyInvitation = new FancyMessage(gravityProfile.getNameWithLitePrefix()).then(" has sent you a ").color(ChatColor.YELLOW).then(gameType.getDisplayName()).color(ChatColor.GOLD).then(" invite ").color(ChatColor.YELLOW).then("[Click to Accept").color(ChatColor.GREEN).tooltip("Click to accept").command("/game accept " + player.getName());

        //Send target invitation
        fancyInvitation.send(target);
        return;
    }
}
