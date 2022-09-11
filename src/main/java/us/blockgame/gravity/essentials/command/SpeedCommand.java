package us.blockgame.gravity.essentials.command;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class SpeedCommand {

    @Command(name = "speed", permission = "op", inGameOnly = true)
    public void speed(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " [player] <speed>");
            return;
        }

        Player target = args.getPlayer();
        if (args.length() > 1) {
            target = Bukkit.getPlayer(args.getArgs(0));

            if (target == null) {
                args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
                return;
            }
        }

        //Make sure entered speed is a number
        if (!NumberUtils.isDigits(args.getArgs(args.length() > 1 ? 1 : 0))) {
            args.getSender().sendMessage(ChatColor.RED + "Please use a valid number.");
            return;
        }
        int multiplier = Integer.parseInt(args.getArgs(args.length() > 1 ? 1 : 0));

        //Make sure entered speed is within the speed range we use
        if (multiplier <= 0 || multiplier > 10) {
            args.getSender().sendMessage(ChatColor.RED + "Please enter a speed between 1 and 10.");
            return;
        }
        float speed = 0.2f * ((multiplier + 1) / 2);

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile targetProfile = profileHandler.getGravityProfile(target);

        //Set player's fly speed
        if (target.isFlying()) {
            target.setFlySpeed(speed);

            args.getSender().sendMessage((target == player ? ChatColor.YELLOW + "Your" : targetProfile.getNameWithLitePrefix() + ChatColor.YELLOW + "'s") + " fly speed has been set to " + ChatColor.GOLD + multiplier + ChatColor.YELLOW + ".");
            return;
        }
        //Set player's walk speed
        target.setWalkSpeed(speed);
        args.getSender().sendMessage((target == player ? ChatColor.YELLOW + "Your" : targetProfile.getNameWithLitePrefix() + ChatColor.YELLOW + "'s") + " walk speed has been set to " + ChatColor.GOLD + multiplier + ChatColor.YELLOW + ".");
        return;
    }
}
