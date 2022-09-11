package us.blockgame.gravity.bungee;

import net.md_5.bungee.BungeeCord;
import us.blockgame.gravity.GravityBungeePlugin;

public class BungeeHandler {

    public BungeeHandler() {
        //Register channels
        GravityBungeePlugin.getInstance().getProxy().registerChannel("msg:gravity");

        //Register listeners
        BungeeCord.getInstance().getPluginManager().registerListener(GravityBungeePlugin.getInstance(), new BungeeListener());
    }

}
