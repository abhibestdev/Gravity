package us.blockgame.gravity.settings;

import lombok.Getter;
import lombok.Setter;
import us.blockgame.gravity.GravityPlugin;

public class SettingsHandler {

    @Setter @Getter private String serverName;
    @Setter @Getter private boolean loginStats;
    @Setter @Getter private boolean logoutStats;

    public SettingsHandler() {
        serverName = GravityPlugin.getInstance().getConfig().getString("settings.server-name");
        loginStats = GravityPlugin.getInstance().getConfig().getBoolean("settings.login-stats");
        logoutStats = GravityPlugin.getInstance().getConfig().getBoolean("settings.logout-stats");
    }
}
