package us.blockgame.gravity.player.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.player.PlayerHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.punishment.PunishmentHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.StringUtil;

public class ReplyCommand {

    @Command(name = "reply", aliases = {"r"}, inGameOnly = true)
    public void reply(CommandArgs args) {

        Player player = args.getPlayer();
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        if (gravityProfile.getLastMessage() == null) {
            args.getSender().sendMessage(ChatColor.RED + "You are not currently engaged in a conversation.");
            return;
        }

        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();

        //Check if user is online
        Player target = Bukkit.getPlayer(gravityProfile.getLastMessage());
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + cacheHandler.getUsername(gravityProfile.getLastMessage()) + " is no longer online.");
            return;
        }

        //If player doesn't provide a message then tell the user who they are replying to
        if (args.length() < 1) {

            RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

            //Don't need to call async because the player is online
            Rank rank = rankHandler.getOfflineRank(gravityProfile.getLastMessage());

            args.getSender().sendMessage(ChatColor.YELLOW + "You are currently in a conversation with " + rank.getLitePrefix() + cacheHandler.getUsername(gravityProfile.getLastMessage()) + ChatColor.YELLOW + ".");
            return;
        }

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

        String message = StringUtil.buildString(args.getArgs(), 0);
        PlayerHandler playerHandler = GravityPlugin.getInstance().getPlayerHandler();

        //Send private message
        playerHandler.sendPrivateMessage(player, target, message);

        //Save the UUID of the last messaged player so they can reply
        gravityProfile.setLastMessage(target.getUniqueId());
        targetProfile.setLastMessage(player.getUniqueId());
        return;
    }
}
