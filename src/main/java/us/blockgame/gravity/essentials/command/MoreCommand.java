package us.blockgame.gravity.essentials.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class MoreCommand {

    @Command(name = "more", permission = "gravity.command.more", inGameOnly = true)
    public void more(CommandArgs args) {
        Player player = args.getPlayer();

        //Check if player is holding air
        if (player.getItemInHand().getType() == Material.AIR) {
            args.getSender().sendMessage(ChatColor.RED + "You are holding air.");
            return;
        }
        //Check if player already has 64 of that item
        if (player.getItemInHand().getAmount() >= 64) {
            args.getSender().sendMessage(ChatColor.RED + "You already have 64 of this item.");
            return;
        }
        //Give more
        player.getItemInHand().setAmount(64);
        player.updateInventory();

        args.getSender().sendMessage(ChatColor.YELLOW + "You have been given more.");
        return;
    }
}
