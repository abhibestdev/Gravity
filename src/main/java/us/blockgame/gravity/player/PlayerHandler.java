package us.blockgame.gravity.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.player.command.*;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;

import java.util.Arrays;

public class PlayerHandler {

    public PlayerHandler() {

        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new PingCommand());
        commandHandler.registerCommand(new ToggleGlobalChatCommand());
        commandHandler.registerCommand(new SoundsCommand());
        commandHandler.registerCommand(new MessageCommand());
        commandHandler.registerCommand(new ReplyCommand());
        commandHandler.registerCommand(new TogglePrivateMessagesCommand());
        commandHandler.registerCommand(new GameModeCommand());
        commandHandler.registerCommand(new ListCommand());
        commandHandler.registerCommand(new IgnoreCommand());
        commandHandler.registerCommand(new UnignoreCommand());

        //Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), GravityPlugin.getInstance());
    }

    public void sendPrivateMessage(Player from, Player to, String message) {
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();

        GravityProfile fromProfile = profileHandler.getGravityProfile(from);
        GravityProfile toProfile = profileHandler.getGravityProfile(to);

        from.sendMessage(ChatColor.GRAY + "(To " + toProfile.getNameWithLitePrefix() + ChatColor.GRAY + ") " + message);
        to.sendMessage(ChatColor.GRAY + "(From " + fromProfile.getNameWithLitePrefix() + ChatColor.GRAY + ") " + message);

        //If player has private messaging noises enabled then play sound
        if (toProfile.isSounds()) {
            to.playSound(to.getLocation(), Sound.ORB_PICKUP, 100.0f, 0.0f);
        }
    }

    public void unsetPermissions(Player player) {
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Don't run if player doesn't have an attachment
        if (gravityProfile.getPermissionAttachment() == null) return;

        PermissionAttachment permissionAttachment = gravityProfile.getPermissionAttachment();

        //Clear permissions
        permissionAttachment.getPermissions().keySet().forEach(p -> permissionAttachment.unsetPermission(p));
        return;
    }

    public void setPermissions(Player player) {
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Don't run if player doesn't have an attachment
        if (gravityProfile.getPermissionAttachment() == null) return;

        Rank rank = gravityProfile.getRank();

        PermissionAttachment permissionAttachment = gravityProfile.getPermissionAttachment();

        //Get all ranks below their current rank
        Arrays.stream(Rank.values()).filter(r -> r.getWeight() >= rank.getWeight()).forEach(r -> {
            //Set permission
            r.getPermissions().forEach(p -> permissionAttachment.setPermission(p, true));
        });
    }

    public void updatePermissions(Player player) {
        unsetPermissions(player);
        setPermissions(player);
    }
}
