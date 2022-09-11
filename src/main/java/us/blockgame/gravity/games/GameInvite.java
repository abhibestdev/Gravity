package us.blockgame.gravity.games;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
public class GameInvite {

    @Getter private UUID sender;
    @Getter private GameType gameType;
    @Getter private final long timestamp = System.currentTimeMillis();
}
