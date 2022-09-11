package us.blockgame.gravity.staff;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.staff.command.*;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.redis.RedisHandler;
import us.blockgame.lib.util.MapUtil;

import java.util.Map;
import java.util.function.Consumer;

public class StaffHandler {

    @Getter private Map<String, Long> pingBack;

    public StaffHandler() {
        //Initialize map
        pingBack = Maps.newHashMap();

        //Register Commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new PlayerInfoCommand());
        commandHandler.registerCommand(new StaffChatCommand());
        commandHandler.registerCommand(new ReportCommand());
        commandHandler.registerCommand(new RequestCommand());
        commandHandler.registerCommand(new AltsCommand());
        commandHandler.registerCommand(new FreezeCommand());

        //Register listeners
        Bukkit.getPluginManager().registerEvents(new StaffListener(), GravityPlugin.getInstance());
    }

    public void sendStaffChat(String playerName, Rank rank, String message, String server) {
        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

        //Pass staffchat packet to redis
        redisHandler.send(MapUtil.createMap(
                "type", "staff-chat",
                "player", playerName,
                "rank", rank.getName(),
                "message", message,
                "server", server));
    }

    public void sendReport(String playerName, String targetName, Rank rank, Rank targetRank, String reason, String server) {
        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

        //Pass report packet to redis
        redisHandler.send(MapUtil.createMap(
                "type", "report",
                "player", playerName,
                "target", targetName,
                "rank", rank.getName(),
                "targetRank", targetRank.getName(),
                "reason", reason,
                "server", server));
    }

    public void sendRequest(String playerName, Rank rank, String reason, String server) {
        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

        //Pass request packet to redis
        redisHandler.send(MapUtil.createMap(
                "type", "request",
                "player", playerName,
                "rank", rank.getName(),
                "reason", reason,
                "server", server));
    }

    public void staffAction(Consumer<? super Player> action) {
        //Make all players with the permission gravity.staff accept the action
        Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("gravity.staff")).forEach(action::accept);
    }

    public void staffBroadcast(String message) {
        //Send all staff message provided
        staffAction(p -> p.sendMessage(message));
    }
}
