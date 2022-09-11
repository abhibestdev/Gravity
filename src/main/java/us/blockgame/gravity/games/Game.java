package us.blockgame.gravity.games;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.gravity.games.impl.rps.RPS;
import us.blockgame.gravity.games.impl.rps.menu.RPSSelectMenu;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.util.CC;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class Game {

    @Getter private final GameType gameType;
    @Getter private final Player p1;
    @Getter private final Player p2;
    @Getter private boolean inProgress;


    public void start() {

        //Start game
        switch (gameType) {
            case RPS: {
                RPS rps = new RPS();

                //Open menus
                new RPSSelectMenu(this, rps, true).openMenu(p1);
                new RPSSelectMenu(this, rps, false).openMenu(p2);

                inProgress = true;
            }
            break;
        }
    }

    public void end(int result) {
        if (!inProgress) return;
        inProgress = false;

        //Game canceled
        if (result == -1) {
            broadcast(" \n" + CC.RED + CC.BOLD + "GAME CANCELED!\n ");
            playSound(Sound.ANVIL_BREAK, 20f, 20f);
            gameAction(p -> {
                if (p.getOpenInventory() != null) p.closeInventory();
            });
        } else {
            new BukkitRunnable() {
                int i = 3;

                public void run() {
                    broadcast(" ");
                    if (i > 0) {

                        String color = (i == 3 ? CC.DARK_RED : i == 2 ? CC.YELLOW : CC.GREEN) + CC.BOLD;

                        broadcast(color + CC.RANDOM + "$$$" + color + " " +  i + " " + color + CC.RANDOM + "$$$");
                        playSound(Sound.NOTE_STICKS, 20f, 20f);

                        i -= 1;
                    } else {
                        broadcast(CC.YELLOW + CC.BOLD + (result == 0 ? "DRAW!" : result == 1 ? p1.getName() + " WINS!" : p2.getName() + " WINS!"));
                        playSound(Sound.NOTE_PLING, 20f, 20f);

                        this.cancel();
                    }
                    broadcast(" ");
                }
            }.runTaskTimerAsynchronously(LibPlugin.getInstance(), 20L, 20L);
        }
    }

    public void gameAction(Consumer<? super Player> action) {
        if (p1.isOnline()) action.accept(p1);
        if (p2.isOnline()) action.accept(p2);
    }

    public void broadcast(String message) {
        gameAction(p -> p.sendMessage(message));
    }

    public void playSound(Sound sound, float v1, float v2) {
        gameAction(p -> p.playSound(p.getLocation(), sound, v1, v2));
    }
}
