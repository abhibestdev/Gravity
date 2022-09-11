package us.blockgame.gravity.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CorrespondingWool {

    BOLD(ChatColor.BOLD, 0),
    RESET(ChatColor.RESET, 0),
    WHITE(ChatColor.WHITE, 0),
    ORANGE(ChatColor.GOLD, 1),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, 2),
    AQUA(ChatColor.AQUA, 3),
    YELLOW(ChatColor.YELLOW, 4),
    GREEN(ChatColor.GREEN, 5),
    GRAY(ChatColor.GRAY, 8),
    DARK_GRAY(ChatColor.DARK_GRAY, 7),
    LIGHT_GRAY(ChatColor.LIGHT_PURPLE, 8),
    DARK_AQUA(ChatColor.DARK_AQUA, 9),
    PURPLE(ChatColor.DARK_PURPLE, 10),
    BLUE(ChatColor.BLUE, 11),
    DARK_GREEN(ChatColor.DARK_GREEN, 13),
    RED(ChatColor.RED, 14),
    DARK_RED(ChatColor.DARK_RED, 14),
    BLACK(ChatColor.BLACK, 15);

    private ChatColor chatColor;
    private int id;

    public static CorrespondingWool getByColor(ChatColor chatColor) {
        return Arrays.stream(CorrespondingWool.values()).filter(c -> c.getChatColor().equals(chatColor)).findFirst().orElse(null);
    }
}
