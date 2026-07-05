package fr.dmall.loupgarou.scoreboard;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.game.WorldManager;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager implements Manager {

    private static final String OBJECTIVE_NAME = "lg_uhc";
    private static final int DIAMOND_TARGET = 17;

    private BukkitTask task;
    private final Map<UUID, Scoreboard> scoreboards = new HashMap<>();

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

        scoreboards.clear();

    }

    public void removePlayer(UUID uuid) {
        scoreboards.remove(uuid);
    }

    private void update() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        WorldManager worldManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(WorldManager.class);

        Game game = gameManager.getCurrentGame();

        boolean started = game.getState() == GameState.DAY || game.getState() == GameState.NIGHT;

        World scoreboardWorld = (worldManager.getGameWorld() != null) ? worldManager.getGameWorld() : Bukkit.getWorlds().get(0);

        String cycle = getCycleLabel(game.getState());
        String duration = started ? formatDuration(System.currentTimeMillis() - game.getStartTimeMillis()) : "-";
        String episode = started ? String.valueOf(game.getEpisode()) : "-";
        String border = formatBorder(scoreboardWorld);
        String players = formatPlayers(game, playerManager, started);
        boolean revealed = game.isRevealed();

        for (Player player : Bukkit.getOnlinePlayers()) {
            applyScoreboard(player, playerManager, duration, cycle, episode, players, border, revealed);
        }

    }

    private Scoreboard getOrCreateScoreboard(Player player) {

        return scoreboards.computeIfAbsent(player.getUniqueId(), uuid -> {

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

            Objective objective = scoreboard.registerNewObjective(
                    OBJECTIVE_NAME,
                    "dummy",
                    "§8§lLoup-Garou §f§lUHC"
            );

            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            player.setScoreboard(scoreboard);

            return scoreboard;

        });

    }

    private void applyScoreboard(Player player, PlayerManager playerManager, String duration, String cycle,
                                  String episode, String players, String border, boolean revealed) {

        Scoreboard scoreboard = getOrCreateScoreboard(player);
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);

        for (String entry : new HashSet<>(scoreboard.getEntries())) {
            scoreboard.resetScores(entry);
        }

        LGPlayer lgPlayer = playerManager.get(player);

        String group = "-";
        String kills = "-";
        String diamonds = "0/" + DIAMOND_TARGET;

        if (lgPlayer != null) {

            RoleTeam team = revealed ? lgPlayer.getEffectiveTeam() : null;

            if (team != null) {
                group = getGroupLabel(team);
            }

            kills = String.valueOf(lgPlayer.getKills());
            diamonds = lgPlayer.getDiamonds() + "/" + DIAMOND_TARGET;

        }

        int line = 9;

        objective.getScore("§e» §fInformations").setScore(line--);
        objective.getScore("§7Durée: §f" + duration).setScore(line--);
        objective.getScore("§7Cycle: §f" + cycle).setScore(line--);
        objective.getScore("§7Épisode: §f" + episode).setScore(line--);
        objective.getScore("§7Groupe: §f" + group).setScore(line--);
        objective.getScore("§7Joueurs: §f" + players).setScore(line--);
        objective.getScore("§7Bordure: §f" + border).setScore(line--);
        objective.getScore("§7Kills: §f" + kills).setScore(line--);
        objective.getScore("§bDiamants: §f" + diamonds).setScore(line--);

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

    private String getGroupLabel(RoleTeam team) {

        if (team == RoleTeam.LOUP) {
            return "Loups";
        }

        if (team == RoleTeam.VILLAGE) {
            return "Village";
        }

        if (team == RoleTeam.AMOUREUX) {
            return "Amoureux";
        }

        return "Solitaire";

    }

    private String formatDuration(long elapsedMillis) {

        long totalSeconds = elapsedMillis / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;

        if (hours > 0) {
            return String.format("%dh%02dm%02ds", hours, minutes, seconds);
        }

        return String.format("%02dm%02ds", minutes, seconds);

    }

    private String formatBorder(World world) {
        return (long) world.getWorldBorder().getSize() + " blocs";
    }

    private String formatPlayers(Game game, PlayerManager playerManager, boolean started) {

        if (!started) {
            return String.valueOf(playerManager.getPlayers().size());
        }

        long alive = game.getPlayers().stream().filter(LGPlayer::isAlive).count();

        return alive + "/" + game.getPlayers().size();

    }

}
