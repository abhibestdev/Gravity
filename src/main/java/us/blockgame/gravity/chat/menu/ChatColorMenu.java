package us.blockgame.gravity.chat.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.chat.ChatHandler;
import us.blockgame.gravity.chat.menu.button.ChatColorButton;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatColorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a chat color";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        ChatHandler chatHandler = GravityPlugin.getInstance().getChatHandler();

        AtomicInteger atomicSlot = new AtomicInteger(0);
        chatHandler.getChatColorList().stream().filter(colorChat -> (!colorChat.isStaffOnly() || (colorChat.isStaffOnly() && player.hasPermission("gravity.staff")))).forEach(colorChat -> {

            //Put chat colors in menu
            buttonMap.put(atomicSlot.getAndAdd(1), new ChatColorButton(colorChat));
        });

        return buttonMap;
    }
}
