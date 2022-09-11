package us.blockgame.gravity.games.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.games.Game;
import us.blockgame.gravity.games.GameInvite;
import us.blockgame.gravity.games.menu.GamesInviteMenu;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class GamesCommand {

    @Command(name = "games", aliases = {"game"}, permission = "gravity.command.game", inGameOnly = true)
    public void games(CommandArgs args) {
        //Send games help
        args.getSender().sendMessage(new String[]{
                ChatColor.RED + "Games Help:",
                ChatColor.RED + " * /games invite <player>",
                ChatColor.RED + " * /games accept <player>",
        });
    }

    @Command(name = "games.invite", aliases = {"game.invite"}, permission = "gravity.command.game", inGameOnly = true)
    public void gamesInvite(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = LibPlugin.getPlayer(args.getArgs(0));
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        Player player = args.getPlayer();

        //Make sure player is not inviting themself
        if (target == player) {
            args.getSender().sendMessage(ChatColor.RED + "You may not invite yourself.");
            return;
        }

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile targetProfile = profileHandler.getGravityProfile(target);

        //Make sure target is not in a game
        if (targetProfile.isPlayingGame()) {
            args.getSender().sendMessage(ChatColor.RED + "That player is already in a game.");
            return;
        }

        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Make sure player is not in a game
        if (gravityProfile.isPlayingGame()) {
            args.getSender().sendMessage(ChatColor.RED + "You are already in a game.");
            return;
        }

        //Look for existing game invite
        GameInvite gameInvite = targetProfile.getGameInviteList().stream().filter(g -> g.getSender().equals(player.getUniqueId()) && System.currentTimeMillis() - g.getTimestamp() <= 30000).findFirst().orElse(null);

        if (gameInvite != null) {
            args.getSender().sendMessage(ChatColor.RED + "You already have an existing game invite out to this player.");
            return;
        }
        GamesInviteMenu gamesInviteMenu = new GamesInviteMenu(target.getUniqueId());

        //Open game invite menu
        gamesInviteMenu.openMenu(player);
        return;
    }

    @Command(name = "games.accept", aliases = {"game.accept"}, permission = "gravity.command.game", inGameOnly = true)
    public void gamesAccept(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args.getArgs(0));
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        Player player = args.getPlayer();

        //Make sure player is not inviting themself
        if (target == player) {
            args.getSender().sendMessage(ChatColor.RED + "You may not invite yourself.");
            return;
        }

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile targetProfile = profileHandler.getGravityProfile(target);

        //Make sure target is not in a game
        if (targetProfile.isPlayingGame()) {
            args.getSender().sendMessage(ChatColor.RED + "That player is already in a game.");
            return;
        }

        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Make sure player is not in a game
        if (gravityProfile.isPlayingGame()) {
            args.getSender().sendMessage(ChatColor.RED + "You are already in a game.");
            return;
        }

        //Look for existing game invite
        GameInvite gameInvite = gravityProfile.getGameInviteList().stream().filter(g -> g.getSender().equals(target.getUniqueId()) && System.currentTimeMillis() - g.getTimestamp() <= 30000).findFirst().orElse(null);

        if (gameInvite == null) {
            args.getSender().sendMessage(ChatColor.RED + "That game invitation was not found.");
            return;
        }
        //Remove game invites
        gravityProfile.getGameInviteList().remove(gameInvite);

        //Start game
        Game game = new Game(gameInvite.getGameType(), player, target);
        game.start();
        return;
    }
}
