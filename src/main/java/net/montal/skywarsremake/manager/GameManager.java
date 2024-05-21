package net.montal.skywarsremake.manager;

import lombok.experimental.UtilityClass;
import net.montal.skywarsremake.object.GamePlayer;
import net.montal.skywarsremake.object.GameState;
import net.montal.skywarsremake.object.GameStateManager;
import net.montal.skywarsremake.utils.TaskManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Store the active games being played on the server
 */
@UtilityClass
public class GameManager {

    private GameStateManager gameStateManager;
    private GameState gameState;
    private final List<SkywarsGame> games = Arrays.asList(new SkywarsGame[12]); // Fixed amount of 12 instances per server

    public boolean createGame() {
        if (atMaxCapacity()) return false;

        SkywarsGame skywarsGame = new SkywarsGame();

        return true;
    }

    public SkywarsGame getGameForPlayer(Player player) {
        UUID targetUUID = player.getUniqueId();

        Callable<SkywarsGame> task = () -> {
            Optional<GamePlayer> foundGamePlayer = games.stream()
                    .flatMap(server -> server.getPlayers().stream())
                    .filter(gamePlayer -> gamePlayer.getUuid().equals(targetUUID))
                    .findFirst();

            if (foundGamePlayer.isEmpty()) {
                return null;
            }

            Optional<SkywarsGame> foundGame = games.stream()
                    .filter(server -> server.getPlayers().contains(foundGamePlayer.get()))
                    .findFirst();

            return foundGame.orElse(null);
        };

        return TaskManager.submit(task);
    }

    public boolean atMaxCapacity() {
        return games.size() == 12;
    }

    public boolean isState(GameState state) {
        return getGameState() == state;
    }

    public void setState(GameState gameState) {
        gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean atMinCapacity() {
        return games.size() == 2;
    }

}
