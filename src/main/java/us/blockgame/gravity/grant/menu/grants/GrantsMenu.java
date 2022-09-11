package us.blockgame.gravity.grant.menu.grants;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.grant.Grant;
import us.blockgame.gravity.grant.GrantHandler;
import us.blockgame.gravity.grant.menu.button.grants.grants.GrantsGrantButton;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.pagination.PaginatedMenu;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class GrantsMenu extends PaginatedMenu {

    private UUID uuid;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Grant History " + ChatColor.GRAY.toString() + ChatColor.BOLD + "[" + getPage() + "/" + getPages(player) + "]";
    }

    @Override
    @SneakyThrows
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        GrantHandler grantHandler = GravityPlugin.getInstance().getGrantHandler();
        List<Grant> grantList = grantHandler.getGrantsFromMongo(uuid);

        //Set items in menu
        for (int i = 0; i < grantList.size(); i++) {
            Grant grant = grantList.get(i);

            buttonMap.put(i, new GrantsGrantButton(uuid, grant));
        }

        return buttonMap;
    }
}
