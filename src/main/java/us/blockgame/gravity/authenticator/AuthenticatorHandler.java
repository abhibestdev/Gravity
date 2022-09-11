package us.blockgame.gravity.authenticator;

import org.bukkit.Bukkit;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.authenticator.command.AuthenticatorCommand;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;

public class AuthenticatorHandler {

    public AuthenticatorHandler() {
        //Register Commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new AuthenticatorCommand());

        //Register listener
        Bukkit.getPluginManager().registerEvents(new AuthenticatorListener(), GravityPlugin.getInstance());
    }
}
