package us.blockgame.gravity.chat.command;

import org.bukkit.entity.Player;
import us.blockgame.gravity.chat.menu.ChatColorMenu;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class ChatColorCommand {

    @Command(name = "chatcolor", permission = "gravity.command.chatcolor", inGameOnly = true)
    public void chatColor(CommandArgs args) {
        Player player = args.getPlayer();

        ChatColorMenu chatColorMenu = new ChatColorMenu();

        //Open chat color menu
        chatColorMenu.openMenu(player);
        return;
    }
}
