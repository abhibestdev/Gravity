package us.blockgame.gravity.disguise.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.CC;
import us.blockgame.lib.util.TimeUtil;

public class AdminDisguiseCommand {

    @Command(name = "admindisguise", aliases = {"adisguise", "admindis", "adis"}, permission = "gravity.command.admindisguise", inGameOnly = true)
    public void adminDisguise(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        String disguise = args.getArgs(0);

        //Make sure name is no longer than characters
        if (disguise.length() > 16) {
            args.getSender().sendMessage(ChatColor.RED + "That name is too long!");
            return;
        }
        Player player = args.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player is on disguise cooldown
        if (System.currentTimeMillis() - gravityProfile.getLastDisguise() <= 5_000) {
            long difference = (gravityProfile.getLastChat() + 5000) - System.currentTimeMillis();
            args.getSender().sendMessage(ChatColor.RED + "You must wait " + TimeUtil.formatTimeMillis(difference, true, true) + " to disguise again.");
            return;
        }

        us.blockgame.lib.disguise.DisguiseHandler disguiseHandler = LibPlugin.getInstance().getDisguiseHandler();
        args.getSender().sendMessage(disguiseHandler.fullDisguise(player, disguise) ? CC.YELLOW + "You are now disguised as " + CC.AQUA + disguiseHandler.getDisguisedName(player) + CC.AQUA + "." + CC.RED + "\nIf the player you are disguised as logs into the server you will be kicked." : CC.RED + "Disguise failed.");

        //Set last ime player disguised
        gravityProfile.setLastDisguise(System.currentTimeMillis());

        //Set visible rank to default
        gravityProfile.setVisibleRank(Rank.DEFAULT);
        return;
    }
}
