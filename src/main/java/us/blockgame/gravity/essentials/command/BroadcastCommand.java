package us.blockgame.gravity.essentials.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.StringUtil;

public class BroadcastCommand {

    @Command(name = "broadcast", aliases = {"bc", "shout", "say", "alert"}, permission = "gravity.command.broadcast")
    public void broadcast(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <message>");
            return;
        }
        String message = StringUtil.buildString(args.getArgs(), 0);

        //Broadcast message
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Alert" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
        return;
    }
}
