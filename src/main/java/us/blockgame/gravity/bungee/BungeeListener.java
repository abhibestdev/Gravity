package us.blockgame.gravity.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.apache.commons.lang3.StringUtils;
import us.blockgame.gravity.GravityBungeePlugin;

public class BungeeListener implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        ServerPing serverPing = event.getResponse();

        //Set MOTD
        String firstLine = ChatColor.translateAlternateColorCodes('&', GravityBungeePlugin.getInstance().getConfig().getString("motd.line1"));
        String secondLine = ChatColor.translateAlternateColorCodes('&', GravityBungeePlugin.getInstance().getConfig().getString("motd.line2"));
        boolean center = GravityBungeePlugin.getInstance().getConfig().getBoolean("motd.center");

        serverPing.setDescription((center ? StringUtils.repeat(' ', (58 - ChatColor.stripColor(firstLine).length()) / 2) : "") + firstLine + "\n" + (center ? StringUtils.repeat(' ', (58 - ChatColor.stripColor(secondLine).length()) / 2) : "") + secondLine);

        event.setResponse(serverPing);
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        //Check if player is joining the proxy
        if (event.getFrom() == null) {
            proxiedPlayer.getServer().sendData("msg:gravity", ("join:" + proxiedPlayer.getServer().getInfo().getName()).getBytes());
            return;
        }
        proxiedPlayer.getServer().sendData("msg:gravity", ("switch:" + event.getFrom().getName() + ":" + proxiedPlayer.getServer().getInfo().getName()).getBytes());
        return;
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        proxiedPlayer.getServer().sendData("msg:gravity", ("quit:" + proxiedPlayer.getServer().getInfo().getName()).getBytes());
    }
}
