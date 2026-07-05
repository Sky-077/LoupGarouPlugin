package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.loup.LoupGarouCraintifRole;
import fr.dmall.loupgarou.role.loup.WolfRole;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class CraintifManager implements Manager {

    private static final double RADIUS = 20.0;
    private static final double SPEED_BONUS = 0.1; // moitié de Speed I (~0.2)
    private static final long TICK_PERIOD_TICKS = 20L * 5L; // recalcul toutes les 5 secondes
    private static final int EFFECT_DURATION_TICKS = (int) TICK_PERIOD_TICKS + 20; // marge pour éviter tout flicker

    private static final NamespacedKey SPEED_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "craintif_speed");

    private BukkitTask task;

    @Override
    public void enable() {

        task = Bukkit.getScheduler().runTaskTimer(
                LoupGarouPlugin.getInstance(),
                this::tick,
                TICK_PERIOD_TICKS,
                TICK_PERIOD_TICKS
        );

    }

    @Override
    public void disable() {

        if (task != null) {
            task.cancel();
        }

    }

    private void tick() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isRevealed() || (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT)) {
            return;
        }

        boolean isNight = game.getState() == GameState.NIGHT;

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive() || !(lgPlayer.getRole() instanceof LoupGarouCraintifRole)) {
                continue;
            }

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player == null) {
                continue;
            }

            int nearbyWolves = countNearbyWolves(game, lgPlayer, player);

            applyEffects(player, nearbyWolves, isNight);

        }

    }

    private int countNearbyWolves(Game game, LGPlayer self, Player selfPlayer) {

        int count = 0;

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive() || !(lgPlayer.getRole() instanceof WolfRole)) {
                continue;
            }

            if (lgPlayer.getUuid().equals(self.getUuid())) {
                count++;
                continue;
            }

            Player other = Bukkit.getPlayer(lgPlayer.getUuid());

            if (other == null || !other.getWorld().equals(selfPlayer.getWorld())) {
                continue;
            }

            if (other.getLocation().distance(selfPlayer.getLocation()) <= RADIUS) {
                count++;
            }

        }

        return count;

    }

    private void applyEffects(Player player, int nearbyWolves, boolean isNight) {

        if (nearbyWolves > 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, EFFECT_DURATION_TICKS, 0, false, false));
        } else {
            player.removePotionEffect(PotionEffectType.WEAKNESS);
        }

        if (nearbyWolves <= 2) {

            if (isNight) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, EFFECT_DURATION_TICKS, 0, false, false));
                player.removePotionEffect(PotionEffectType.RESISTANCE);
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, EFFECT_DURATION_TICKS, 0, false, false));
                player.removePotionEffect(PotionEffectType.STRENGTH);
            }

        } else {
            player.removePotionEffect(PotionEffectType.RESISTANCE);
            player.removePotionEffect(PotionEffectType.STRENGTH);
        }

        if (nearbyWolves == 1) {
            grantSpeedBonus(player);
        } else {
            removeSpeedBonus(player);
        }

    }

    private void grantSpeedBonus(Player player) {

        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (speed == null || speed.getModifiers().stream().anyMatch(modifier -> modifier.getKey().equals(SPEED_KEY))) {
            return;
        }

        speed.addModifier(new AttributeModifier(SPEED_KEY, SPEED_BONUS, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

    }

    private void removeSpeedBonus(Player player) {

        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (speed == null) {
            return;
        }

        speed.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(SPEED_KEY))
                .forEach(speed::removeModifier);

    }

    public void clear(Player player) {

        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.RESISTANCE);
        removeSpeedBonus(player);

    }

}
