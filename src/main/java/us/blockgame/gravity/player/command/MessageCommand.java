package us.blockgame.gravity.player.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.player.PlayerHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.punishment.PunishmentHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.StringUtil;

public class MessageCommand {

    @Command(name = "message", aliases = {"m", "msg", "whisper", "w", "tell"}, inGameOnly = true)
    public void message(CommandArgs args) {
        if (args.length() < 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <message>");
            return;
        }
        Player target = LibPlugin.getPlayer(args.getArgs(0));
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        Player player = args.getPlayer();
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        PunishmentHandler punishmentHandler = GravityPlugin.getInstance().getPunishmentHandler();

        //Check if player is muted
        if (punishmentHandler.isMuted(player)) {
            args.getSender().sendMessage(ChatColor.RED + "You are currently muted.");
            return;
        }

        //Check if player has private messaging disabled
        if (!gravityProfile.isPrivateMessaging()) {
            args.getSender().sendMessage(ChatColor.RED + "You have private messaging disabled.");
            return;
        }

        //Check if they are ignoring the target
        if (gravityProfile.getIgnored().contains(target.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "You are currently ignoring that player.");
            return;
        }

        GravityProfile targetProfile = profileHandler.getGravityProfile(target);

        //Check if the target is ignoring the sender
        if (targetProfile.getIgnored().contains(player.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "That player is currently ignoring you.");
            return;
        }

        //Check if target has private messaging disabled
        if (!targetProfile.isPrivateMessaging()) {
            args.getSender().sendMessage(ChatColor.RED + "That player is currently not accepting private messages.");
            return;
        }

        //Check if target is ignoring the player
        if (targetProfile.getIgnored().contains(player.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "That player is currently ignoring you.");
            return;
        }

        String message = StringUtil.buildString(args.getArgs(), 1);
        PlayerHandler playerHandler = GravityPlugin.getInstance().getPlayerHandler();

        //Send private message
        playerHandler.sendPrivateMessage(player, target, message);

        //Save the UUID of the last messaged player so they can reply
        gravityProfile.setLastMessage(target.getUniqueId());
        targetProfile.setLastMessage(player.getUniqueId());
        return;
    }
}
