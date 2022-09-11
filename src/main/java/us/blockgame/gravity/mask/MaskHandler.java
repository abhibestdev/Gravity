package us.blockgame.gravity.mask;

import us.blockgame.gravity.mask.command.MaskCommand;
import us.blockgame.gravity.mask.command.UnmaskCommand;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;

public class MaskHandler {

    public MaskHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new MaskCommand());
        commandHandler.registerCommand(new UnmaskCommand());
    }
}
