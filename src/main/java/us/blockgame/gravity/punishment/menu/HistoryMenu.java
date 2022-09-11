package us.blockgame.gravity.punishment.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import us.blockgame.gravity.punishment.menu.button.history.HistoryPunishmentButton;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.pagination.PaginatedMenu;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class HistoryMenu extends PaginatedMenu {

    private UUID uuid;
    private JSONObject punishmentsObject;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "History " + ChatColor.GRAY.toString() + ChatColor.BOLD + "[" + getPage() + "/" + getPages(player) + "]";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        List<HistoryPunishment> historyPunishments = new ArrayList<>();

        AtomicInteger atomicSlot = new AtomicInteger();
        punishmentsObject.keySet().forEach(o -> {
            historyPunishments.add(new HistoryPunishment(Long.parseLong(o.toString()), (JSONObject) punishmentsObject.get(o)));
        });

        //Sort punishments in ascending order
        historyPunishments.sort(Comparator.comparing(HistoryPunishment::getTime));

        historyPunishments.forEach(h -> {
           buttonMap.put(atomicSlot.getAndAdd(1), new HistoryPunishmentButton(uuid, h.getJsonObject()));
        });
        return buttonMap;
    }


    @AllArgsConstructor
    private class HistoryPunishment {

        @Getter private long time;
        @Getter private JSONObject jsonObject;
    }
}
