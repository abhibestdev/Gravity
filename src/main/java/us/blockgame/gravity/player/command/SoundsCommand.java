package us.blockgame.gravity.player.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class SoundsCommand {

    @Command(name = "sounds", inGameOnly = true)
    public void sounds(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Toggle private messaging notifications
        gravityProfile.setSounds(!gravityProfile.isSounds());

        args.getSender().sendMessage(ChatColor.YELLOW + "You are " + (gravityProfile.isSounds() ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + ChatColor.YELLOW + " receiving private messaging notifications.");
        return;
    }
}
