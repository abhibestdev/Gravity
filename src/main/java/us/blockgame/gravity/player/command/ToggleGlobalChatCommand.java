package us.blockgame.gravity.player.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class ToggleGlobalChatCommand {

    @Command(name = "toggleglobalchat", aliases = {"tgc", "togglegc"}, inGameOnly = true)
    public void toggleGlobalChat(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Toggle global chat
        gravityProfile.setGlobalChat(!gravityProfile.isGlobalChat());

        args.getSender().sendMessage(ChatColor.YELLOW + "You are " + (gravityProfile.isGlobalChat() ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + ChatColor.YELLOW + " viewing global chat.");
        return;
    }
}
