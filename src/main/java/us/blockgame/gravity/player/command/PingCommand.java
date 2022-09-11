package us.blockgame.gravity.player.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.util.reflection.BukkitReflection;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class PingCommand {

    @Command(name = "ping", inGameOnly = true)
    public void ping(CommandArgs args) {
        Player target = null;

        if (args.length() < 1) {
            target = args.getPlayer();
        } else {
            target = LibPlugin.getPlayer(args.getArgs(0));
        }

        //Check if player is online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }

        int ping = BukkitReflection.getPing(target);

        //Set color that we want to display the ping depending on how high it is
        ChatColor severity = ChatColor.GREEN;

        if (ping < 100) {
            severity = ChatColor.GREEN;
        } else if (ping < 150) {
            severity = ChatColor.YELLOW;
        } else {
            severity = ChatColor.RED;
        }

        //Send separate message if player is target. Not in the same method because we would need to fetch rank if not.
        if (target == args.getPlayer()) {
            args.getSender().sendMessage(ChatColor.YELLOW + "Your ping is " + severity + ping + ChatColor.YELLOW + " ms.");
            return;
        }
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(target);

        args.getSender().sendMessage(gravityProfile.getNameWithLitePrefix() + ChatColor.YELLOW + "'s ping is " + severity + ping + ChatColor.YELLOW + " ms.");
        return;
    }
}
