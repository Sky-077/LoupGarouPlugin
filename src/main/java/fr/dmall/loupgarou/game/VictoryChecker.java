package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.RoleTeam;

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
        long amoureuxAlive = countAlive(game, RoleTeam.AMOUREUX);

        if (loupsAlive == 0 && soloAlive == 0 && amoureuxAlive == 0 && villageAlive > 0) {
            endGame("§aLe Village a gagné ! Tous les Loups-Garous et les joueurs solitaires ont été éliminés.");
            return;
        }

        if (villageAlive == 0 && soloAlive == 0 && amoureuxAlive == 0 && loupsAlive > 0) {
            endGame("§cLes Loups-Garous ont gagné ! Tous les villageois et les joueurs solitaires ont été éliminés.");
            return;
        }

        if (amoureuxAlive > 0 && loupsAlive == 0 && villageAlive == 0 && soloAlive == 0) {
            endGame("§dLes Amoureux ont gagné ! Ils sont les seuls survivants.");
        }

    }

    private static long countAlive(Game game, RoleTeam team) {

        return game.getPlayers().stream()
                .filter(LGPlayer::isAlive)
                .filter(lgPlayer -> lgPlayer.getEffectiveTeam() == team)
                .count();

    }

    private static void endGame(String message) {

        GameEnder.end(
                "§6==================================================",
                message,
                "§6=================================================="
        );

    }

}
