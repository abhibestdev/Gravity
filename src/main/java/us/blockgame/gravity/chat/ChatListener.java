package us.blockgame.gravity.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.punishment.PunishmentHandler;
import us.blockgame.gravity.punishment.impl.MutePunishment;
import us.blockgame.lib.util.TimeUtil;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        PunishmentHandler punishmentHandler = GravityPlugin.getInstance().getPunishmentHandler();

        if (punishmentHandler.isMuted(player)) {

            MutePunishment mutePunishment = gravityProfile.getMutePunishment();

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are currently muted for " + mutePunishment.getReason() + ". This mute " + (mutePunishment.getDuration() == -1L ? "will never expire" : "will expire in " + TimeUtil.formatTimeMillis(mutePunishment.getDuration() + mutePunishment.getMuteTime() - System.currentTimeMillis(), false, true)) + ".");
            return;
        }
        ChatHandler chatHandler = GravityPlugin.getInstance().getChatHandler();

        //Check if the player is not staff
        if (!player.hasPermission("gravity.staff")) {

            //Check if public chat is muted
            if (chatHandler.isChatMute()) {
                event.setCancelled(true);

                player.sendMessage(ChatColor.RED + "Public chat is currently muted.");
                return;
            }

            //Check if player is on chat cooldown
            if (System.currentTimeMillis() - gravityProfile.getLastChat() <= chatHandler.getChatDelay() && !player.hasPermission("gravity.chat.bypass")) {
                event.setCancelled(true);

                long difference = (gravityProfile.getLastChat() + chatHandler.getChatDelay()) - System.currentTimeMillis();
                player.sendMessage(ChatColor.RED + "You must wait " + TimeUtil.formatTimeMillis(difference, true, true) + " to chat again.");
                return;
            }

            Bukkit.getOnlinePlayers().forEach(p -> {
                GravityProfile playerProfile = profileHandler.getGravityProfile(p);

                //Check if the receiver has global chat off or is currently ignoring this player
                if (!playerProfile.isGlobalChat() || playerProfile.getIgnored().contains(player.getUniqueId())) event.getRecipients().remove(p);
            });
        }

        ChatColor chatColor = ChatColor.WHITE;

        ColorChat colorChat = gravityProfile.getColorChat();
        if (colorChat != null) {

            //Make sure player has permission to use colored chat
            if ((colorChat.isStaffOnly() && player.hasPermission("gravity.staff")) || (!colorChat.isStaffOnly() && player.hasPermission("gravity.donor"))) {
                chatColor = colorChat.getChatColor();
            }
        }

        event.setFormat(gravityProfile.getNameWithPrefix() + ChatColor.RESET + ": " + chatColor + event.getMessage().replace("%", "%%"));
        return;
    }
}
