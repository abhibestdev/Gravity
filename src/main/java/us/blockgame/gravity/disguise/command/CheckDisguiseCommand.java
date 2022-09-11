package us.blockgame.gravity.disguise.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class CheckDisguiseCommand {

    @Command(name = "checkdisguise", aliases = {"cdisguise", "checkdis", "cdis", "realname"}, permission = "gravity.command.checkdisguise", inGameOnly = true)
    public void checkDisguise(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args.getArgs(0));
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        us.blockgame.lib.disguise.DisguiseHandler disguiseHandler = LibPlugin.getInstance().getDisguiseHandler();

        //Check if player is disguised
        if (!disguiseHandler.isDisguised(target)) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not disguised.");
            return;
        }
        args.getSender().sendMessage(ChatColor.GOLD + target.getName() + "'s " + ChatColor.YELLOW + "real name is " + ChatColor.GOLD + disguiseHandler.getRealName(target) + ChatColor.YELLOW + ".");
        return;
    }
}
