package us.blockgame.gravity;

import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import us.blockgame.gravity.bungee.BungeeHandler;

import java.io.File;

public class GravityBungeePlugin extends Plugin {

    @Getter private static GravityBungeePlugin instance;

    @Getter private Configuration config;

    @Getter private BungeeHandler bungeeHandler;

    @Override
    public void onEnable() {
        //Create instance
        instance = this;

        //Create config
        createConfig();

        //Register handlers
        registerHandlers();
    }

    @SneakyThrows
    private void createConfig() {
        File gravityFolder = new File(getProxy().getPluginsFolder() + File.separator + "Gravity");

        //If gravity folder doesn't exist, create one
        if (!gravityFolder.exists()) {
            gravityFolder.mkdir();
        }

        File configFile = new File(gravityFolder.getAbsolutePath() + File.separator + "config.yml");

        //If config file doesn't exist, create one
        boolean justCreated = false;
        if (!configFile.exists()) {
            justCreated = configFile.createNewFile();
        }

        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

        //If the file was just created, set the config defaults
        if (justCreated) {
            config.set("motd.line1", "&eGravity");
            config.set("motd.line2", "&cThe best core ever!");
            config.set("motd.center", true);

            //Save config
            saveConfig();
        }
    }

    @SneakyThrows
    public void saveConfig() {
        File configFile = new File(getProxy().getPluginsFolder() + File.separator + "Gravity" + File.separator + "config.yml");

        //Save config
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
    }

    private void registerHandlers() {
        bungeeHandler = new BungeeHandler();
    }
}
