package us.blockgame.gravity.rank.command;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.ChatColor;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.redis.RedisHandler;
import us.blockgame.lib.util.CC;
import us.blockgame.lib.util.MapUtil;

import java.util.Map;

public class RankCommand {

    @Command(name = "rank", aliases = {"group"}, permission = "gravity.command.rank")
    public void rank(CommandArgs args) {
        //Send rank help
        args.getSender().sendMessage(new String[]{
                ChatColor.RED + "Rank Commands: ",
                ChatColor.RED + " * /rank addPerm <rank> <permission>",
                ChatColor.RED + " * /rank delPerm <rank> <permission>",
                ChatColor.RED + " * /rank listPerm <rank>"
        });
    }

    @Command(name = "rank.addperm", aliases = {"group.addperm"}, permission = "gravity.command.rank", runAsync = true)
    public void rankAddPerm(CommandArgs args) {
        if (args.length() < 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <rank> <permission>");
            return;
        }
        Rank rank = Rank.getRank(args.getArgs(0));

        //Check if rank exists
        if (rank == null) {
            args.getSender().sendMessage(ChatColor.RED + "That rank doesn't exist.");
            return;
        }
        String permission = args.getArgs(1);

        //Check if rank already has that permission
        if (rank.getPermissions().contains(permission)) {
            args.getSender().sendMessage(ChatColor.RED + "That rank already has that permission.");
            return;
        }

        //Add permission to rank
        rank.getPermissions().add(permission);

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("ranks");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", rank.toString())).first();

        //Delete document if it exists
        if (document != null) {
            mongoCollection.deleteOne(document);
        }

        Map<String, Object> documentMap = MapUtil.createMap(
                "_id", rank.toString(),
                "permissions", rank.getPermissions()
        );

        //Insert new document into mongo
        mongoCollection.insertOne(new Document(documentMap));

        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

        Map<String, Object> messageMap = MapUtil.createMap(
                "type", "rank-add-perm",
                "rank", rank.toString(),
                "permission", permission
        );

        //Send permission update to redis
        redisHandler.send(messageMap);

        args.getSender().sendMessage(ChatColor.YELLOW + "You have granted the rank " + rank.getLitePrefix() + rank.getDisplayName() + ChatColor.YELLOW + " the permission " + ChatColor.AQUA + permission + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "rank.delperm", aliases = {"group.delperm"}, permission = "gravity.command.rank", runAsync = true)
    public void rankDelPerm(CommandArgs args) {
        if (args.length() < 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <rank> <permission>");
            return;
        }
        Rank rank = Rank.getRank(args.getArgs(0));

        //Check if rank exists
        if (rank == null) {
            args.getSender().sendMessage(ChatColor.RED + "That rank doesn't exist.");
            return;
        }
        String permission = args.getArgs(1);

        //Check if rank already has that permission
        if (!rank.getPermissions().contains(permission)) {
            args.getSender().sendMessage(ChatColor.RED + "That rank doesn't have that permission.");
            return;
        }
        rank.getPermissions().remove(permission);

        MongoHandler mongoHandler = GravityPlugin.getInstance().getMongoHandler();
        MongoCollection mongoCollection = mongoHandler.getCollection("ranks");

        Document document = (Document) mongoCollection.find(Filters.eq("_id", rank.toString())).first();

        //Delete document if it exists
        if (document != null) {
            mongoCollection.deleteOne(document);
        }

        Map<String, Object> documentMap = MapUtil.createMap(
                "_id", rank.toString(),
                "permissions", rank.getPermissions()
        );

        //Insert new document into mongo
        mongoCollection.insertOne(new Document(documentMap));

        RedisHandler redisHandler = LibPlugin.getInstance().getRedisHandler();

        Map<String, Object> messageMap = MapUtil.createMap(
                "type", "rank-del-perm",
                "rank", rank.toString(),
                "permission", permission
        );

        //Send permission update to redis
        redisHandler.send(messageMap);

        args.getSender().sendMessage(ChatColor.YELLOW + "You have revoked the rank " + rank.getLitePrefix() + rank.getDisplayName() + ChatColor.YELLOW + " of the permission " + ChatColor.AQUA + permission + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "rank.listperm", aliases = {"group.listperm"}, permission = "gravity.command.rank", runAsync = true)
    public void rankListPerm(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <rank>");
            return;
        }
        Rank rank = Rank.getRank(args.getArgs(0));

        //Check if rank exists
        if (rank == null) {
            args.getSender().sendMessage(ChatColor.RED + "That rank doesn't exist.");
            return;
        }
        if (rank.getPermissions().size() > 0) {
            args.getSender().sendMessage(CC.GRAY + CC.STRIKE + "-----------------------------------");
            args.getSender().sendMessage(rank.getLitePrefix() + rank.getDisplayName() + "'s " + ChatColor.GOLD + "permissions: ");
            rank.getPermissions().stream().forEach(p -> {
                args.getSender().sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + p);
            });
            args.getSender().sendMessage(CC.GRAY + CC.STRIKE + "-----------------------------------");
        } else {
            args.getSender().sendMessage(ChatColor.RED + "No permissions found for that rank.");
        }
    }
 }
