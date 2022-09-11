package us.blockgame.gravity.chat;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.chat.command.ChatColorCommand;
import us.blockgame.gravity.chat.command.ChatCommand;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;

import java.util.List;

public class ChatHandler {

    @Setter @Getter private long chatDelay;
    @Setter @Getter private boolean chatMute;
    @Getter private List<ColorChat> chatColorList;

    public ChatHandler() {
        //Create Chat colors
        chatColorList = ImmutableList.of(
                new ColorChat("White", ChatColor.WHITE, false),
                new ColorChat("Red", ChatColor.RED, false),
                new ColorChat("Green", ChatColor.GREEN, false),
                new ColorChat("Blue", ChatColor.BLUE, false),
                new ColorChat("Yellow", ChatColor.YELLOW, false),
                new ColorChat("Aqua", ChatColor.AQUA, true)
        );

        //Register Commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new ChatCommand());
        commandHandler.registerCommand(new ChatColorCommand());

        //Register Listeners
        Bukkit.getPluginManager().registerEvents(new ChatListener(), GravityPlugin.getInstance());
    }
}
