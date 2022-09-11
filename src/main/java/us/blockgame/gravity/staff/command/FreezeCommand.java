package us.blockgame.gravity.staff.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class FreezeCommand {

    @Command(name = "freeze", aliases = {"ss"}, permission = "gravity.command.freeze")
    public void freeze(CommandArgs args) {

        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = LibPlugin.getPlayer(args.getArgs(0));

        //Check if player is offline
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        //Check if player is trying to freeze themself
        if (args.isPlayer() && args.getPlayer() == target && !target.hasMetadata("frozen")) {
            args.getSender().sendMessage(ChatColor.RED + "You may not freeze yourself.");
            return;
        }

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile targetProfile = profileHandler.getGravityProfile(target);

        //Check if player is frozen
        if (target.hasMetadata("frozen")) {
            //Unfreeze player
            target.removeMetadata("frozen", GravityPlugin.getInstance());

            target.sendMessage(ChatColor.GREEN + "You have been unfrozen.");
            args.getSender().sendMessage(ChatColor.GREEN + "You have unfroze " + ChatColor.RESET + targetProfile.getNameWithLitePrefix() + ChatColor.GREEN + ".");
            return;
        }
        target.setMetadata("frozen", new FixedMetadataValue(GravityPlugin.getInstance(), true));
        args.getSender().sendMessage(ChatColor.GREEN + "You have froze " + ChatColor.RESET + targetProfile.getNameWithLitePrefix() + ChatColor.GREEN + ".");

        //Message task
        new BukkitRunnable() {
            public void run() {

                //If target logged off or is no longer frozen, cancel the task
                if (!target.isOnline() || !target.hasMetadata("frozen")) {
                    this.cancel();
                    return;
                }
                target.sendMessage(new String[]{
                        " ",
                        " ",
                        " ",
                        " ",
                        ChatColor.RED + "You have been frozen by a staff member!",
                        ChatColor.RED + "Please join teamspeak: " + ChatColor.GRAY + "ts.gravity.com" + ChatColor.RED + "!",
                        " ",
                        " ",
                        " ",
                        " "
                });
            }
        }.runTaskTimerAsynchronously(GravityPlugin.getInstance(), 0L, 20L);
        return;
    }
}
