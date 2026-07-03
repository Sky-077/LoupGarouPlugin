package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class CycleManager implements Manager {

    private static final long NIGHT_START = 13000L;
    private static final long NIGHT_END = 23000L;

    private BukkitTask task;

    @Override
    public void enable() {

        task = Bukkit.getScheduler().runTaskTimer(
                LoupGarouPlugin.getInstance(),
                this::tick,
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

    }

    public GameState getPhaseForCurrentTime() {
        return isNight(getWorld().getTime()) ? GameState.NIGHT : GameState.DAY;
    }

    private void tick() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT) {
            return;
        }

        GameState newState = getPhaseForCurrentTime();

        if (newState != game.getState()) {
            game.setState(newState);
            onPhaseChange(game, newState);
        }

    }

    private void onPhaseChange(Game game, GameState newState) {

        if (newState == GameState.DAY) {
            game.incrementEpisode();
        }

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive() || lgPlayer.getRole() == null) {
                continue;
            }

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player == null) {
                continue;
            }

            if (newState == GameState.DAY) {
                lgPlayer.getRole().onDay(player);
            } else {
                lgPlayer.getRole().onNight(player);
            }

        }

        String message = (newState == GameState.DAY)
                ? "§eLe jour se lève."
                : "§9La nuit tombe...";

        Bukkit.broadcastMessage(message);

    }

    private boolean isNight(long time) {
        return time >= NIGHT_START && time < NIGHT_END;
    }

    private World getWorld() {
        return Bukkit.getWorlds().get(0);
    }

}