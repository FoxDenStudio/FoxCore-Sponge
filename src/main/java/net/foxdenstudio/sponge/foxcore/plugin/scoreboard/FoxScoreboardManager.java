package net.foxdenstudio.sponge.foxcore.plugin.scoreboard;

import net.foxdenstudio.sponge.foxcore.common.util.WeakCacheMap;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class FoxScoreboardManager {

    private static FoxScoreboardManager instance = new FoxScoreboardManager();

    Map<Player, Deque<FoxScoreboard>> playerScoreboards = new WeakCacheMap<>((k, m) -> {
        if(k instanceof Player){
            Player player = (Player) k;
            Deque<FoxScoreboard> deque = new LinkedList<>();
            m.put(player, deque);
            return deque;
        } else return null;
    });

    public static FoxScoreboardManager getInstance() {
        return instance;
    }

    public void display(Player player, FoxScoreboard scoreboard) {
        Deque<FoxScoreboard> scoreboards = playerScoreboards.get(player);
        scoreboards.remove(scoreboard);
        scoreboards.addFirst(scoreboard);
        //player.setScoreboard(scoreboard);
    }

    public Optional<FoxScoreboard> getActiveScoreboard(Player player) {
        return Optional.ofNullable(playerScoreboards.get(player).peekFirst());
    }

}
