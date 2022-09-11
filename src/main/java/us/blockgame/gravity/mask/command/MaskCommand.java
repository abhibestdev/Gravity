package us.blockgame.gravity.mask.command;

import org.bukkit.entity.Player;
import us.blockgame.gravity.mask.menu.MaskMenu;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class MaskCommand {

    @Command(name = "mask", permission = "gravity.command.mask", inGameOnly = true)
    public void mask(CommandArgs args) {
        Player player = args.getPlayer();

        //Open menu
        MaskMenu maskMenu = new MaskMenu();
        maskMenu.openMenu(player);
    }
}
