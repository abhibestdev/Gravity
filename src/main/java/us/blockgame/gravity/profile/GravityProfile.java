package us.blockgame.gravity.profile;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import us.blockgame.gravity.chat.ColorChat;
import us.blockgame.gravity.games.GameInvite;
import us.blockgame.gravity.grant.Grant;
import us.blockgame.gravity.punishment.impl.MutePunishment;
import us.blockgame.gravity.rank.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class GravityProfile {

    @Getter private UUID uuid;
    @Setter @Getter private Rank rank;
    @Setter @Getter private boolean globalChat;
    @Setter @Getter private boolean sounds;
    @Setter @Getter private List<UUID> ignored;
    @Setter @Getter private boolean privateMessaging;
    @Setter @Getter private UUID lastMessage;
    @Setter @Getter private List<Grant> grantList;
    @Setter @Getter private List<GameInvite> gameInviteList;
    @Setter @Getter private boolean playingGame;
    @Setter @Getter private Rank visibleRank;
    @Setter @Getter private long lastChat;
    @Setter @Getter private ColorChat colorChat;
    @Setter @Getter private long lastDisguise;
    @Setter @Getter private boolean staffChat;
    @Setter @Getter private long lastReport;
    @Setter @Getter private long lastRequest;
    @Setter @Getter private Rank lastVisibleRank;
    @Setter @Getter private MutePunishment mutePunishment;
    @Setter @Getter private long loginTime;
    @Setter @Getter private List<String> ips;
    @Setter @Getter private boolean loginFromNewIp;
    @Setter @Getter private String gAuthKey;
    @Setter @Getter private boolean gAuthEnabled;
    @Setter @Getter private boolean passed2fa;
    @Setter @Getter private PermissionAttachment permissionAttachment;

    public GravityProfile(UUID uuid) {
        this.uuid = uuid;
        //Set rank to default automatically
        rank = Rank.DEFAULT;
        //Enable global chat by default
        globalChat = true;
        //Enable private messaging notifications by default
        sounds = true;
        //Set ignored to empty list by default
        ignored = new ArrayList<>();
        //Enable private messaging by default
        privateMessaging = true;
        //Set grants to empty list by default
        grantList = new ArrayList<>();
        //Set game invites to an empty list
        gameInviteList = new ArrayList<>();
        //Set ips to empty list by default
        ips = new ArrayList<>();
    }

    public String getNameWithPrefix() {
        Player player = Bukkit.getPlayer(uuid);

        return getUsedRank().getPrefix() + player.getName();
    }

    public String getNameWithLitePrefix() {
        Player player = Bukkit.getPlayer(uuid);

        return getUsedRank().getLitePrefix() + player.getName();
    }

    public Rank getUsedRank() {
        //If player is masking, use this rank
        if (visibleRank != null) return visibleRank;

        return rank;
    }
}
