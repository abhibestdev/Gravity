package us.blockgame.gravity.staff.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.settings.SettingsHandler;
import us.blockgame.gravity.staff.StaffHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.StringUtil;

public class StaffChatCommand {

    @Command(name = "staffchat", aliases = {"sc"}, permission = "gravity.command.staffchat", inGameOnly = true)
    public void staffChat(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        if (args.length() < 1) {
            //Toggle staff chat
            gravityProfile.setStaffChat(!gravityProfile.isStaffChat());
            args.getSender().sendMessage(gravityProfile.isStaffChat() ? ChatColor.YELLOW + "You have entered staff chat." : ChatColor.RED + "You have exited staff chat.");
            return;
        }
        String message = StringUtil.buildString(args.getArgs(), 0);

        StaffHandler staffHandler = GravityPlugin.getInstance().getStaffHandler();
        SettingsHandler settingsHandler = GravityPlugin.getInstance().getSettingsHandler();

        //Send staff chat to redis
        staffHandler.sendStaffChat(player.getName(), gravityProfile.getUsedRank(), message, settingsHandler.getServerName());
        return;
    }
}
