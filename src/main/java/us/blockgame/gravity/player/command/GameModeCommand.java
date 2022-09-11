package us.blockgame.gravity.player.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class GameModeCommand {

    @Command(name = "gamemode", aliases = {"gm"}, permission = "gravity.command.gamemode", inGameOnly = true)
    public void gamemode(CommandArgs args) {
        args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <gamemode>");
        return;
    }

    @Command(name = "gamemode.s", aliases = {"gms", "gm0", "gm.s", "gm.0", "gamemode.0"}, permission = "gravity.command.gamemode", inGameOnly = true)
    public void survival(CommandArgs args) {
        Player player = args.getPlayer();

        Player target = null;

        if (args.length() == 0) {
            target = player;
        } else {
            target = Bukkit.getPlayer(args.getArgs(0));
        }

        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        target.setGameMode(GameMode.SURVIVAL);

        if (target != player) {
            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
            GravityProfile gravityProfile = profileHandler.getGravityProfile(target);

            args.getSender().sendMessage(gravityProfile.getNameWithLitePrefix() + ChatColor.YELLOW + "'s gamemode has been updated to " + ChatColor.GOLD + "Survival" + ChatColor.YELLOW + ".");
        }
        args.getSender().sendMessage(ChatColor.YELLOW + "Your gamemode has been updated to " + ChatColor.GOLD + "Survival" + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "gamemode.c", aliases = {"gmc", "gm1", "gm.c", "gm.1", "gamemode.1"}, permission = "gravity.command.gamemode", inGameOnly = true)
    public void creative(CommandArgs args) {
        Player player = args.getPlayer();

        Player target = null;

        if (args.length() == 0) {
            target = player;
        } else {
            target = Bukkit.getPlayer(args.getArgs(0));
        }

        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        target.setGameMode(GameMode.CREATIVE);

        if (target != player) {
            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
            GravityProfile gravityProfile = profileHandler.getGravityProfile(target);

            args.getSender().sendMessage(gravityProfile.getNameWithLitePrefix() + ChatColor.YELLOW + "'s gamemode has been updated to " + ChatColor.GOLD + "Creative" + ChatColor.YELLOW + ".");
        }
        args.getSender().sendMessage(ChatColor.YELLOW + "Your gamemode has been updated to " + ChatColor.GOLD + "Creative" + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "gamemode.a", aliases = {"gma", "gm2", "gm.a", "gm.2", "gamemode.2"}, permission = "gravity.command.gamemode", inGameOnly = true)
    public void adventure(CommandArgs args) {
        Player player = args.getPlayer();

        Player target = null;

        if (args.length() == 0) {
            target = player;
        } else {
            target = Bukkit.getPlayer(args.getArgs(0));
        }

        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        target.setGameMode(GameMode.ADVENTURE);

        if (target != player) {
            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
            GravityProfile gravityProfile = profileHandler.getGravityProfile(target);

            args.getSender().sendMessage(gravityProfile.getNameWithLitePrefix() + ChatColor.YELLOW + "'s gamemode has been updated to " + ChatColor.GOLD + "Adventure" + ChatColor.YELLOW + ".");
        }
        args.getSender().sendMessage(ChatColor.YELLOW + "Your gamemode has been updated to " + ChatColor.GOLD + "Adventure" + ChatColor.YELLOW + ".");
        return;
    }
}
