package us.blockgame.gravity.disguise;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import us.blockgame.gravity.disguise.command.AdminDisguiseCommand;
import us.blockgame.gravity.disguise.command.CheckDisguiseCommand;
import us.blockgame.gravity.disguise.command.DisguiseCommand;
import us.blockgame.gravity.disguise.command.UndisguiseCommand;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.util.WebUtil;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class DisguiseHandler {

    @Getter private List<String> adjectives;
    @Getter private List<String> nouns;

    public DisguiseHandler() {
        //Register Commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new DisguiseCommand());
        commandHandler.registerCommand(new AdminDisguiseCommand());
        commandHandler.registerCommand(new UndisguiseCommand());
        commandHandler.registerCommand(new CheckDisguiseCommand());

        //Run async so we don't hog up the thread
        CompletableFuture.runAsync(() -> {
            //Get adjectives from URL
            adjectives = WebUtil.getLines("https://gist.githubusercontent.com/hugsy/8910dc78d208e40de42deb29e62df913/raw/eec99c5597a73f6a9240cab26965a8609fa0f6ea/english-adjectives.txt");

            //Get nouns from URL
            nouns = WebUtil.getLines("https://gist.githubusercontent.com/hugsy/8910dc78d208e40de42deb29e62df913/raw/eec99c5597a73f6a9240cab26965a8609fa0f6ea/english-nouns.txt");
        });
    }

    public String randomName() {
        Random random = LibPlugin.getRandom();

        //Get random adjective from the list
        String adjective = adjectives.get(random.nextInt(adjectives.size()));

        //Get random noun from the list
        String noun = nouns.get(random.nextInt(nouns.size()));

        //Generate random number between 1 and 999;
        int numbers = LibPlugin.getRandom().nextInt(999) + 1;

        String newName = adjective + noun + (LibPlugin.getRandom().nextBoolean() ? numbers : "");

        //If the name isn't alphanumeric or shorter than 16 characters, generate a new one
        if (!StringUtils.isAlphanumeric(newName) || newName.length() > 16) return randomName();

        return newName;
    }
}
