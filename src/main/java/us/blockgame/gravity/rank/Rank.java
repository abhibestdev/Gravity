package us.blockgame.gravity.rank;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Rank {

    OWNER("Owner", "Owner", "&7[&4Owner&7] &4", "&4", true, 0),
    MANAGER("Manager", "Manager", "&7[&4&oManager&7] &4&o", "&4&o", true, 1),
    ADMIN("Admin", "Admin", "&7[&cAdmin&7] &c", "&c", true, 2),
    MOD("Moderator", "Moderator", "&7[&3Moderator&7] &3", "&3", true, 3),
    CHAT_MOD("Chat-Mod", "Chat-Mod", "&7[&eChat&7-&eMod&7] &e", "&e", true, 4),
    FAMOUS("Famous", "Famous", "&7[&d&oFamous&7] &d&o", "&d", false, 5),
    YOUTUBE("YouTube", "YouTube", "&7[&dYouTube&7] &d", "&d&o", false, 6),
    MINEMAN("Mineman", "Mineman", "&7[&6Mineman&7] &6", "&6", false, 7),
    PLATINUM("Platinum", "Platinum", "&7[&bPlatinum&7] &b", "&b", false, 8),
    DEFAULT("Default", "Default", "&f", "&f", false, 999);

    @Getter private String name;
    @Getter private String displayName;
    private String prefix;
    private String litePrefix;
    @Getter private boolean staff;
    @Getter private int weight;
    @Setter @Getter private List<String> permissions;

    Rank(String name, String displayName, String prefix, String litePrefix, boolean staff, int weight) {
        this.name = name;
        this.displayName = displayName;
        this.prefix = prefix;
        this.litePrefix = litePrefix;
        this.staff = staff;
        this.weight = weight;

        //Set permissions to empty list
        permissions = new ArrayList<>();
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getLitePrefix() {
        return ChatColor.translateAlternateColorCodes('&', litePrefix);
    }

    public static Rank getRank(String name) {
        return Arrays.stream(values()).filter(r -> r.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
