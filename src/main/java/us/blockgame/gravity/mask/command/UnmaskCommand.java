package us.blockgame.gravity.mask.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class UnmaskCommand {

    @Command(name = "unmask", permission = "gravity.command.unmask", inGameOnly = true)
    public void unmask(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Make sure player is masked
        if (gravityProfile.getVisibleRank() == null) {
            args.getSender().sendMessage(ChatColor.RED + "You are not masked.");
            return;
        }
        //Remove player's mask
        gravityProfile.setVisibleRank(null);
        args.getSender().sendMessage(ChatColor.YELLOW + "You are no longer masked.");
        return;
    }
}
