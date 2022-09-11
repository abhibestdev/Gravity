package us.blockgame.gravity.staff.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.settings.SettingsHandler;
import us.blockgame.gravity.staff.StaffHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.StringUtil;
import us.blockgame.lib.util.TimeUtil;

public class RequestCommand {

    @Command(name = "request", aliases = {"helpop"}, inGameOnly = true)
    public void request(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <reason>");
            return;
        }
        Player player = args.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        if (System.currentTimeMillis() - gravityProfile.getLastRequest() <= 120000) {
            long difference = (gravityProfile.getLastRequest() + 120000) - System.currentTimeMillis();

            args.getSender().sendMessage(ChatColor.RED + "You must wait " + TimeUtil.formatTimeMillis(difference, true, true) + " to request again.");
            return;
        }

        String reason = StringUtil.buildString(args.getArgs(), 0);

        SettingsHandler settingsHandler = GravityPlugin.getInstance().getSettingsHandler();

        StaffHandler staffHandler = GravityPlugin.getInstance().getStaffHandler();

        //Set last request time
        gravityProfile.setLastRequest(System.currentTimeMillis());

        //Send request to redis
        staffHandler.sendRequest(player.getName(), gravityProfile.getUsedRank(), reason, settingsHandler.getServerName());
        args.getSender().sendMessage(ChatColor.GREEN + "We have received your request.");
        return;
    }
}
