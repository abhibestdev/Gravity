package us.blockgame.gravity.grant.menu.button.grant.confirmation;

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
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.lib.util.MapUtil;
import us.blockgame.lib.util.TimeUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public class GrantConfirmationConfirmButton extends Button {

    private UUID uuid;
    private Rank rank;
    private String reason;
    private long duration;

    @Override
    public ItemStack getButtonItem(Player player) {

        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        String username = cacheHandler.getUsername(uuid);

        return new ItemBuilder(Material.WOOL)
                .setDurability((short) 5)
                .setName(ChatColor.GREEN + "Confirm Grant")
                .setLore(
                        ChatColor.YELLOW + "Grant " + ChatColor.RESET + username + ChatColor.YELLOW + " the " + rank.getLitePrefix() + rank.getDisplayName() + ChatColor.YELLOW + " rank",
                        ChatColor.GOLD + " * " + ChatColor.YELLOW + "Duration: " + ChatColor.GOLD + TimeUtil.formatTimeMillis(duration, false, true),
                        ChatColor.GOLD + " * " + ChatColor.YELLOW + "Reason: " + ChatColor.GOLD + reason
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        Player target = Bukkit.getPlayer(uuid);

        //Set players rank if they are online the server

        long grantTime = System.currentTimeMillis();

        if (target != null) {
            ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
            GravityProfile gravityProfile = profileHandler.getGravityProfile(target);

            RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();

            //Add grant to grants list and the grants task will automatically update the players rank
            gravityProfile.getGrantList().add(new Grant(grantTime, duration, reason, rank, uuid, rankHandler.getOfflineRank(player.getUniqueId())));
        }
        player.closeInventory();

        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        String username = cacheHandler.getUsername(uuid);

        CompletableFuture.runAsync(() -> {
            MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("players");

            Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

            //Check if the player's document exists
            if (document == null) {
                player.sendMessage(ChatColor.RED + "Data for this player was not found.");
                return;
            }

            Map<String, Object> documentMap = MapUtil.cloneDocument(document);

            JSONObject jsonDocument = null;
            try {
                jsonDocument = (JSONObject) new JSONParser().parse(document.toJson());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONObject grants = jsonDocument.containsKey("grants") ? (JSONObject) jsonDocument.get("grants") : new JSONObject();

            //Create grant JSONObject
            JSONObject grant = new JSONObject();
            grant.put("rank", rank.getName());
            grant.put("reason", reason);
            grant.put("duration", String.valueOf(duration));
            grant.put("uuid", player.getUniqueId().toString());

            grants.put(String.valueOf(grantTime), grant);

            //Delete current document
            mongoCollection.deleteOne(document);

            documentMap.put("grants", grants);
            documentMap.put("rank", rank.getName());

            //Input new document into mongo
            mongoCollection.insertOne(new Document(documentMap));
        });

        player.sendMessage(ChatColor.YELLOW + "You have granted " + rank.getLitePrefix() + username + ChatColor.YELLOW + " the " + rank.getLitePrefix() + rank.getDisplayName() + ChatColor.YELLOW + " for " + ChatColor.GOLD + TimeUtil.formatTimeMillis(duration, false, true) + ChatColor.YELLOW + " for " + ChatColor.GOLD + reason + ChatColor.YELLOW + ".");
        return;
    }
}
