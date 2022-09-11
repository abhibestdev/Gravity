package us.blockgame.gravity.player.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ListCommand {

    @Command(name = "list", aliases = {"who"})
    public void list(CommandArgs args) {

        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();

        List<ListPlayer> listPlayerList = new ArrayList<>();
        List<Rank> ranksList = new ArrayList<>();

        Arrays.stream(Rank.values()).forEach(r -> ranksList.add(r));

        Bukkit.getOnlinePlayers().forEach(p -> {
            GravityProfile gravityProfile = profileHandler.getGravityProfile(p);

            listPlayerList.add(new ListPlayer(p.getName(), gravityProfile.getUsedRank()));
        });

        //Sort ranks and players in descending order
        listPlayerList.sort(Comparator.comparing(l -> l.getRank().getWeight()));
        ranksList.sort(Comparator.comparing(r -> r.getWeight()));

        //Colored rank names
        List<String> formattedRanks = new ArrayList<>();
        ranksList.forEach(r -> formattedRanks.add(r.getLitePrefix() + r.getDisplayName()));

        //Colored player names
        List<String> formattedNames = new ArrayList<>();
        listPlayerList.forEach(l -> formattedNames.add(l.getRank().getLitePrefix() + l.getName()));

        args.getSender().sendMessage(new String[]{
                StringUtil.join(formattedRanks, ChatColor.GRAY + ", "),
                ChatColor.GRAY + "(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ") " + ChatColor.RESET + StringUtil.join(formattedNames, ChatColor.GRAY + ", ")
        });
    }

    @AllArgsConstructor
    public class ListPlayer {

        @Getter private String name;
        @Getter private Rank rank;

    }
}
