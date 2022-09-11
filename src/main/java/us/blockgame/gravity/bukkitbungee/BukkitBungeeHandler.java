package us.blockgame.gravity.bukkitbungee;

import org.bukkit.Bukkit;
import us.blockgame.gravity.GravityPlugin;

public class BukkitBungeeHandler {

    public BukkitBungeeHandler() {

        //Create listener instance
        BukkitBungeeListener bukkitBungeeListener = new BukkitBungeeListener();

        //Register channels
        Bukkit.getMessenger().registerIncomingPluginChannel(GravityPlugin.getInstance(), "BungeeCord", bukkitBungeeListener);
        Bukkit.getMessenger().registerIncomingPluginChannel(GravityPlugin.getInstance(), "msg:gravity", bukkitBungeeListener);
        Bukkit.getMessenger().registerOutgoingPluginChannel(GravityPlugin.getInstance(), "BungeeCord");
        Bukkit.getMessenger().registerOutgoingPluginChannel(GravityPlugin.getInstance(), "msg:gravity");

        //Register listeners
        Bukkit.getPluginManager().registerEvents(bukkitBungeeListener, GravityPlugin.getInstance());
    }
}
