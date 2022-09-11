package us.blockgame.gravity.essentials;

import us.blockgame.gravity.essentials.command.BroadcastCommand;
import us.blockgame.gravity.essentials.command.MoreCommand;
import us.blockgame.gravity.essentials.command.SpeedCommand;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;

public class EssentialsHandler {

    public EssentialsHandler() {

        //Register Commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new SpeedCommand());
        commandHandler.registerCommand(new BroadcastCommand());
        commandHandler.registerCommand(new MoreCommand());
    }
}
