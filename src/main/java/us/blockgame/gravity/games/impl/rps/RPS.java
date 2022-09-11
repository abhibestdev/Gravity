package us.blockgame.gravity.games.impl.rps;

import lombok.Getter;
import lombok.Setter;

public class RPS {

    @Setter @Getter private RPSType p1Type;
    @Setter @Getter private RPSType p2Type;

    public int compare() {
        switch (p1Type) {
            case ROCK: {
                switch (p2Type) {
                    case ROCK: {
                        return 0;
                    }
                    case PAPER: {
                        return 2;
                    }
                    case SCISSORS: {
                        return 1;
                    }
                }
                break;
            }
            case PAPER: {
                switch (p2Type) {
                    case ROCK: {
                        return 1;
                    }
                    case PAPER: {
                        return 0;
                    }
                    case SCISSORS: {
                        return 2;
                    }
                }
                break;
            }
            case SCISSORS: {
                switch (p2Type) {
                    case ROCK: {
                        return 2;
                    }
                    case PAPER: {
                        return 1;
                    }
                    case SCISSORS: {
                        return 0;
                    }
                }
            }
            break;
        }
        return 0;
    }
}
