package us.blockgame.gravity.chat.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.chat.ChatHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.TimeUtil;

public class ChatCommand {

    @Command(name = "chat", permission = "gravity.command.chat")
    public void chat(CommandArgs args) {
        //Send chat help
        args.getSender().sendMessage(new String[]{
                ChatColor.RED + "Chat Help",
                ChatColor.RED + " * /chat clear",
                ChatColor.RED + " * /chat slow",
                ChatColor.RED + " * /chat mute",
        });
    }

    @Command(name = "chat.clear", aliases = {"clearchat", "cc"}, permission = "gravity.command.chat")
    public void chatClear(CommandArgs args) {
        //Send 1000 blank messages to everyone that doesn't have the permission gravity.staff
        Bukkit.getOnlinePlayers().stream().filter(p -> !p.hasPermission("gravity.staff")).forEach(p -> {
            for (int i = 0; i <= 1000; i++) p.sendMessage(" ");
        });
        Bukkit.broadcastMessage(ChatColor.RED + "The chat has been cleared by " + args.getSender().getName() + ".");
        return;
    }

    @Command(name = "chat.slow", aliases = {"slowchat", "chatslow"}, permission = "gravity.command.chat")
    public void chatSlow(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <delay>");
            return;
        }
        long delay = TimeUtil.parseTime(args.getArgs(0));

        //Make sure chat slow is not perm
        if (delay == -1L) {
            args.getSender().sendMessage(ChatColor.RED + "Please enter a valid time.");
            return;
        }
        ChatHandler chatHandler = GravityPlugin.getInstance().getChatHandler();

        //Set chat delay
        chatHandler.setChatDelay(delay);
        Bukkit.broadcastMessage(ChatColor.RED + "Chat slow has been updated to " + TimeUtil.formatTimeMillis(delay, false, true) + ".");
        return;
    }

    @Command(name = "chat.mute", aliases = {"chatmute", "mutechat", "mc"}, permission = "gravity.command.chat")
    public void chatMute(CommandArgs args) {
        ChatHandler chatHandler = GravityPlugin.getInstance().getChatHandler();

        //Toggle chat mute
        chatHandler.setChatMute(!chatHandler.isChatMute());
        Bukkit.broadcastMessage(chatHandler.isChatMute() ? ChatColor.RED + "Public chat has been muted." : ChatColor.GREEN + "Public chat is no longer muted.");
        return;
    }
}
