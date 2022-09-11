package us.blockgame.gravity.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class ColorChat {

    @Getter private String name;
    @Getter private ChatColor chatColor;
    @Getter private boolean staffOnly;
}
