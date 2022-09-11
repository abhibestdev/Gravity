package us.blockgame.gravity.disguise.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.CC;

public class UndisguiseCommand {

    @Command(name = "undisguise", aliases = {"undis"}, permission = "gravity.command.undisguise", inGameOnly = true)
    public void undisguise(CommandArgs args) {
        Player player = args.getPlayer();

        us.blockgame.lib.disguise.DisguiseHandler disguiseHandler = LibPlugin.getInstance().getDisguiseHandler();

        if (player.hasMetadata("disguise")) {
            args.getSender().sendMessage(ChatColor.RED + "You may not undisguise now.");
            return;
        }

        //Make sure player is disguised
        if (!disguiseHandler.isDisguised(player)) {
            args.getSender().sendMessage(ChatColor.RED + "You are not disguised.");
            return;
        }
        //Undisguise player
        disguiseHandler.undisguise(player);
        //Set rank back to their normal rank
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);
        gravityProfile.setVisibleRank(null);

        player.sendMessage(CC.RED + "You are no longer disguised.");
        return;
    }
}
