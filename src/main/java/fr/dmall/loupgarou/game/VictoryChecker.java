package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;

public class VictoryChecker {

    private VictoryChecker() {
    }

    public static void check() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        long loupsAlive = countAlive(game, RoleTeam.LOUP);
        long villageAlive = countAlive(game, RoleTeam.VILLAGE);
        long soloAlive = countAlive(game, RoleTeam.NEUTRAL);

        if (loupsAlive == 0 && soloAlive == 0) {
            endGame(game, "§aLe Village a gagné ! Tous les Loups-Garous et les joueurs solitaires ont été éliminés.");
            return;
        }

        if (villageAlive == 0 && soloAlive == 0) {
            endGame(game, "§cLes Loups-Garous ont gagné ! Tous les villageois et les joueurs solitaires ont été éliminés.");
        }

    }

    private static long countAlive(Game game, RoleTeam team) {

        return game.getPlayers().stream()
                .filter(LGPlayer::isAlive)
                .filter(lgPlayer -> lgPlayer.getRole() != null && lgPlayer.getRole().getTeam() == team)
                .count();

    }

    private static void endGame(Game game, String message) {

        game.setState(GameState.FINISHED);

        Bukkit.broadcastMessage("§6==================================================");
        Bukkit.broadcastMessage(message);
        Bukkit.broadcastMessage("§6==================================================");

    }

}
