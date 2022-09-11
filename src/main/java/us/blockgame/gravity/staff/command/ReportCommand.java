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

public class ReportCommand {

    @Command(name = "report", aliases = {"complain"}, inGameOnly = true)
    public void report(CommandArgs args) {
        if (args.length() < 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <reason>");
            return;
        }
        Player target = LibPlugin.getPlayer(args.getArgs(0));

        //Make sure player is online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        Player player = args.getPlayer();

        if (target == player) {
            args.getSender().sendMessage(ChatColor.RED + "You may not report yourself!");
            return;
        }

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        if (System.currentTimeMillis() - gravityProfile.getLastReport() <= 120000) {
            long difference = (gravityProfile.getLastReport() + 120000) - System.currentTimeMillis();

            args.getSender().sendMessage(ChatColor.RED + "You must wait " + TimeUtil.formatTimeMillis(difference, true, true) + " to report again.");
            return;
        }

        GravityProfile targetProfile = profileHandler.getGravityProfile(target);
        String reason = StringUtil.buildString(args.getArgs(), 1);

        SettingsHandler settingsHandler = GravityPlugin.getInstance().getSettingsHandler();

        StaffHandler staffHandler = GravityPlugin.getInstance().getStaffHandler();

        //Set last report time
        gravityProfile.setLastReport(System.currentTimeMillis());

        //Send report to redis
        staffHandler.sendReport(player.getName(), target.getName(), gravityProfile.getUsedRank(), targetProfile.getUsedRank(), reason, settingsHandler.getServerName());
        args.getSender().sendMessage(ChatColor.GREEN + "We have received your report.");
        return;
    }
}
