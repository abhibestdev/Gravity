package us.blockgame.gravity;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import us.blockgame.gravity.authenticator.AuthenticatorHandler;
import us.blockgame.gravity.board.BoardHandler;
import us.blockgame.gravity.broadcaster.BroadcasterHandler;
import us.blockgame.gravity.bukkitbungee.BukkitBungeeHandler;
import us.blockgame.gravity.chat.ChatHandler;
import us.blockgame.gravity.disguise.DisguiseHandler;
import us.blockgame.gravity.essentials.EssentialsHandler;
import us.blockgame.gravity.games.GamesHandler;
import us.blockgame.gravity.grant.GrantHandler;
import us.blockgame.gravity.mask.MaskHandler;
import us.blockgame.gravity.mongo.MongoHandler;
import us.blockgame.gravity.player.PlayerHandler;
import us.blockgame.gravity.profile.ProfileHandler;
import us.blockgame.gravity.punishment.PunishmentHandler;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.gravity.settings.SettingsHandler;
import us.blockgame.gravity.staff.StaffHandler;
import us.blockgame.gravity.tab.TabHandler;
import us.blockgame.gravity.tag.TagHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.economy.EconomyType;

public class GravityPlugin extends JavaPlugin {

    @Getter private static GravityPlugin instance;
    @Setter @Getter private static boolean tabOverride;
    @Setter @Getter private static boolean scoreboardOverride;
    @Getter private static GoogleAuthenticator googleAuthenticator;

    @Getter private MongoHandler mongoHandler;
    @Getter private SettingsHandler settingsHandler;
    @Getter private ProfileHandler profileHandler;
    @Getter private RankHandler rankHandler;
    @Getter private ChatHandler chatHandler;
    @Getter private TabHandler tabHandler;
    @Getter private GrantHandler grantHandler;
    @Getter private PlayerHandler playerHandler;
    @Getter private StaffHandler staffHandler;
    @Getter private AuthenticatorHandler authenticatorHandler;
    @Getter private EssentialsHandler essentialsHandler;
    @Getter private DisguiseHandler disguiseHandler;
    @Getter private GamesHandler gamesHandler;
    @Getter private MaskHandler maskHandler;
    @Getter private PunishmentHandler punishmentHandler;
    @Getter private BroadcasterHandler broadcasterHandler;
    @Getter private BoardHandler boardHandler;
    @Getter private TagHandler tagHandler;
    @Getter private BukkitBungeeHandler bukkitBungeeHandler;

    @Override
    public void onEnable() {
        instance = this;
        googleAuthenticator = new GoogleAuthenticator();

        //Create files
        getConfig().options().copyDefaults(true);
        saveConfig();

        //Register Handlers
        registerHandlers();

        //Register economy
       // registerEconomy();
    }

    @Override
    public void onDisable() {
        //Kick everyone when server stops
        Bukkit.getOnlinePlayers().forEach(p -> p.kickPlayer(ChatColor.RED + "Server is restarting."));
    }

    private void registerHandlers() {
        mongoHandler = new MongoHandler();
        settingsHandler = new SettingsHandler();
        profileHandler = new ProfileHandler();
        rankHandler = new RankHandler();
        chatHandler = new ChatHandler();
        tabHandler = new TabHandler();
        grantHandler = new GrantHandler();
        playerHandler = new PlayerHandler();
        staffHandler = new StaffHandler();
    //    authenticatorHandler = new AuthenticatorHandler();
        essentialsHandler = new EssentialsHandler();
        disguiseHandler = new DisguiseHandler();
        gamesHandler = new GamesHandler();
        maskHandler = new MaskHandler();
        punishmentHandler = new PunishmentHandler();
        broadcasterHandler = new BroadcasterHandler();
        boardHandler = new BoardHandler();
        tagHandler = new TagHandler();
        bukkitBungeeHandler = new BukkitBungeeHandler();
    }

    private void registerEconomy() {
        LibPlugin.getInstance().getEconomyHandler().initializeCurrency(EconomyType.POINTS);
    }
}
