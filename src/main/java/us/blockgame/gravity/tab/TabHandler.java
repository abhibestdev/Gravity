package us.blockgame.gravity.tab;

import org.bukkit.Bukkit;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.util.Logger;

public class TabHandler {

    public TabHandler() {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GravityPlugin.getInstance(), () -> {
            if (!GravityPlugin.isTabOverride()) {

                //Initialize Gravity Tab
                us.blockgame.lib.tab.TabHandler tabHandler = LibPlugin.getInstance().getTabHandler();
                tabHandler.setTab(new GravityTab());
                tabHandler.initialize();

                Logger.info("No tab override was set. Using Gravity tab.");
                return;
            }
        }, 20L);
    }
}
