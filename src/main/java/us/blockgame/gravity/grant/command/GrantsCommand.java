package us.blockgame.gravity.grant.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.grant.menu.grants.GrantsMenu;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

import java.util.UUID;

public class GrantsCommand {

    @Command(name = "grants", permission = "gravity.command.grants", inGameOnly = true)
    public void grants(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        //Make sure player has logged into the server before
        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }
        Player player = args.getPlayer();

        //Open menu
        GrantsMenu grantsMenu = new GrantsMenu(uuid);
        grantsMenu.openMenu(player);
        return;
    }
}
