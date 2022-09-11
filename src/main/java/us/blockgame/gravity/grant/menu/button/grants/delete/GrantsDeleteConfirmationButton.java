package us.blockgame.gravity.grant.menu.button.grants.delete;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.grant.Grant;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class GrantsDeleteConfirmationButton extends Button {

    private UUID uuid;
    private Grant grant;

    @Override
    public ItemStack getButtonItem(Player player) {
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();

        return new ItemBuilder(Material.WOOL)
                .setDurability((short) 5)
                .setName(ChatColor.GREEN + "Confirm Deletion")
                .setLore(
                        ChatColor.YELLOW + "Delete Grant from " + ChatColor.GOLD + DateUtil.millisToDate(grant.getTime()) + ChatColor.YELLOW + ": ",
                        ChatColor.GOLD + " * " + ChatColor.YELLOW + "Granted By: " + ChatColor.RESET + cacheHandler.getUsername(grant.getUuid()),
                        ChatColor.GOLD + " * " + ChatColor.YELLOW + "Duration: " + ChatColor.GOLD + TimeUtil.formatTimeMillis(grant.getDuration(), false, true),
                        ChatColor.GOLD + " * " + ChatColor.YELLOW + "Reason: " + ChatColor.GOLD + grant.getReason()
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        Player target = Bukkit.getPlayer(uuid);

        //Delete the grant from their saved list of grants if they are online
        if (target != null) {
            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
            GravityProfile gravityProfile = profileHandler.getGravityProfile(target);

            Grant foundGrant = gravityProfile.getGrantList().stream().filter(g -> g.getTime() == grant.getTime()).findFirst().orElse(null);

            //Remove found grant
            if (foundGrant != null) gravityProfile.getGrantList().remove(foundGrant);
        }

        CompletableFuture.runAsync(() -> {
            MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("players");

            Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

            if (document != null) {
                JSONObject jsonDocument = null;
                try {
                    jsonDocument = (JSONObject) new JSONParser().parse(document.toJson());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                JSONObject grants = (JSONObject) jsonDocument.get("grants");
                Object key = grants.keySet().stream().filter(t -> t.toString().equalsIgnoreCase(String.valueOf(grant.getTime()))).findFirst().orElse(null);

                //Delete grant
                if (key != null) {
                    grants.remove(key);

                    Map<String, Object> documentMap = MapUtil.cloneDocument(document);
                    documentMap.put("grants", grants);

                    //Delete existing document
                    mongoCollection.deleteOne(document);

                    //Insert new document
                    mongoCollection.insertOne(new Document(documentMap));

                    ThreadUtil.runSync(() -> {
                        player.closeInventory();
                        player.sendMessage(ChatColor.YELLOW + "You have successfully deleted a grant.");
                    });
                }
            }
        });
    }
}
