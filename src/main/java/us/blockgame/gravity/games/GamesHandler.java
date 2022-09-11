package us.blockgame.gravity.games;

import us.blockgame.gravity.games.command.GamesCommand;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;

public class GamesHandler {

    public GamesHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new GamesCommand());
    }
}
