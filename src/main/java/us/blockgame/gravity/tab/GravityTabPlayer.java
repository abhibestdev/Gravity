package us.blockgame.gravity.tab;

import lombok.AllArgsConstructor;
import lombok.Getter;
import us.blockgame.gravity.rank.Rank;

@AllArgsConstructor
public class GravityTabPlayer {

    @Getter private String name;
    @Getter private Rank rank;

}
