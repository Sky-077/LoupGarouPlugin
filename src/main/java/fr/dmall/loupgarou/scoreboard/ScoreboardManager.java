package fr.dmall.loupgarou.scoreboard;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager implements Manager {

    private static final String OBJECTIVE_NAME = "lg_uhc";

    private BukkitTask task;

    @Override
    public void enable() {

        task = Bukkit.getScheduler().runTaskTimer(
                LoupGarouPlugin.getInstance(),
                this::update,
                0L,
                20L
        );

    }

    @Override
    public void disable() {

        if (task != null) {
            task.cancel();
            task = null;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

    }

    private void update() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        Game game = gameManager.getCurrentGame();

        int playerCount = playerManager.getPlayers().size();
        String cycle = getCycleLabel(game.getState());

        for (Player player : Bukkit.getOnlinePlayers()) {
            applyScoreboard(player, playerCount, cycle);
        }

    }

    @SuppressWarnings("deprecation")
    private void applyScoreboard(Player player, int playerCount, String cycle) {

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective(
                OBJECTIVE_NAME,
                "dummy",
                "§8§lLoup-Garou §f§lUHC"
        );

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int line = 4;

        objective.getScore("§e» §fInformations").setScore(line--);
        objective.getScore("§7Joueurs: §f" + playerCount).setScore(line--);
        objective.getScore("§7Cycle: §f" + cycle).setScore(line--);

        player.setScoreboard(scoreboard);

    }

    private String getCycleLabel(GameState state) {

        if (state == GameState.DAY) {
            return "Jour";
        }

        if (state == GameState.NIGHT) {
            return "Nuit";
        }

        return "En attente";

    }

}