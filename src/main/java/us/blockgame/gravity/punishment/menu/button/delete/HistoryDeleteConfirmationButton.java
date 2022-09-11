package us.blockgame.gravity.punishment.menu.button.delete;

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
import us.blockgame.lib.util.DateUtil;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.lib.util.MapUtil;
import us.blockgame.lib.util.TimeUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class HistoryDeleteConfirmationButton extends Button {

    private UUID uuid;
    private JSONObject punishmentObject;

    @Override
    public ItemStack getButtonItem(Player player) {
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();

        String reason = (String) punishmentObject.get("reason");
        long duration = punishmentObject.containsKey("duration") ? Long.parseLong((String) punishmentObject.get("duration")) : -2L;
        boolean perm = duration == -1L;
        long time = Long.parseLong((String) punishmentObject.get("time"));
        UUID punisher = UUID.fromString((String) punishmentObject.get("uuid"));

        return new ItemBuilder(Material.WOOL)
                .setDurability((short) 5)
                .setName(ChatColor.GREEN + "Confirm Deletion")
                .setLore(
                        ChatColor.YELLOW + "Delete Punishment from " + ChatColor.GOLD + DateUtil.millisToDate(time) + ChatColor.YELLOW + ": ",
                        ChatColor.GOLD + " * " + ChatColor.YELLOW + "Punished By: " + ChatColor.RESET + cacheHandler.getUsername(punisher),
                        ChatColor.GOLD + " * " + ChatColor.YELLOW + "Duration: " + ChatColor.GOLD +  (duration == -2L || perm ? "Forever" : TimeUtil.formatTimeMillis(time, false, true)),
                        ChatColor.GOLD + " * " + ChatColor.YELLOW + "Reason: " + ChatColor.GOLD + reason
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();

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

                JSONObject punishments = (JSONObject) jsonDocument.get("punishments");
                Object key = punishments.keySet().stream().filter(t -> t.toString().equalsIgnoreCase(punishmentObject.get("time").toString())).findFirst().orElse(null);

                //Delete punishment
                if (key != null) {
                    punishments.remove(key);

                    Map<String, Object> documentMap = MapUtil.cloneDocument(document);
                    documentMap.put("punishments", punishments);

                    //Delete existing document
                    mongoCollection.deleteOne(document);

                    //Insert new document
                    mongoCollection.insertOne(new Document(documentMap));

                    player.closeInventory();
                    player.sendMessage(ChatColor.YELLOW + "You have successfully deleted a punishment.");
                }
            }
        });

        String type = (String) punishmentObject.get("type");

        //If player deletes a blacklist, unblacklist them
        if (type.equalsIgnoreCase("blacklist")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unblacklist " + cacheHandler.getUsername(uuid));
        }
    }
}
