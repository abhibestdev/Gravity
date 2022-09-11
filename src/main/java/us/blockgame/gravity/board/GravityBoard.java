package us.blockgame.gravity.board;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.scoreboard.BGBoard;

import java.util.ArrayList;
import java.util.List;

public class GravityBoard implements BGBoard {

    @Override
    public String getTitle(Player player) {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "Gravity " + ChatColor.GRAY + "%splitter% " + ChatColor.RESET + "Dev";
    }

    @Override
    public List<String> getSlots(Player player) {
        List<String> slots = new ArrayList<>();
        slots.add("&7&m------------------------");
        slots.add("&fDrip's a gay man lol!!");
        slots.add("&7&m------------------------");
        return slots;
    }

    @Override
    public long getUpdateInterval() {
        return 1L;
    }
}
