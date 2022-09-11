package us.blockgame.gravity.disguise.command;

import javafx.scene.control.Skin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.disguise.DisguiseHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.CC;
import us.blockgame.lib.util.MojangUtil;
import us.blockgame.lib.util.TimeUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DisguiseCommand {

    @Command(name = "disguise", aliases = {"dis", "d"}, permission = "gravity.command.disguise", inGameOnly = true)
    public void disguise(CommandArgs args) {
        DisguiseHandler disguiseHandler = GravityPlugin.getInstance().getDisguiseHandler();
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();

        Player player = args.getPlayer();

        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        //Check if player is on disguise cooldown
        if (System.currentTimeMillis() - gravityProfile.getLastDisguise() <= 5_000) {
            long difference = (gravityProfile.getLastChat() + 5000) - System.currentTimeMillis();
            args.getSender().sendMessage(ChatColor.RED + "You must wait " + TimeUtil.formatTimeMillis(difference, true, true) + " to disguise again.");
            return;
        }

        String randomUsername = cacheHandler.randomUsername();
        String[] skin = MojangUtil.getSkin(randomUsername);

        //Make sure skin exists.
        if (skin == null) {
            args.getSender().sendMessage(ChatColor.RED + "Disguise failed.");
            return;
        }

        //Set the player's new name
        LibPlugin.getInstance().getDisguiseHandler().changeName(player, disguiseHandler.randomName());
        //Set the player's skin
        LibPlugin.getInstance().getDisguiseHandler().changeSkin(player, skin[0], skin[1], skin[2]);
        //Set the last time player disguised
        gravityProfile.setLastDisguise(System.currentTimeMillis());

        args.getSender().sendMessage(CC.YELLOW + "You are now disguised as " + CC.AQUA + LibPlugin.getInstance().getDisguiseHandler().getDisguisedName(player) + CC.PRIMARY + "." + CC.RED + "\nIf the player you are disguised as logs into the server you will be kicked.");

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

        //Set visible rank to default
        gravityProfile.setVisibleRank(Rank.DEFAULT);
        return;
    }
}
