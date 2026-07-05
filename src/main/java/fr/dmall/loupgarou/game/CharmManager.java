package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.item.FluteItem;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.solo.JoueurDeFluteRole;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CharmManager implements Manager {

    private static final double RADIUS = 20.0;
    private static final double MAIN_RATE_PER_SECOND = 1.0 / 4.0;
    private static final double CARRIER_RATE_PER_SECOND = 1.0 / 8.0;
    private static final double MELEE_BONUS = 10.0;
    private static final double HEART_BONUS = 2.0; // HP, soit 1 cœur
    private static final double SPEED_BONUS = 0.1; // moitié de Speed I (~0.2)

    private static final NamespacedKey HEART_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "flute_hearts");
    private static final NamespacedKey SPEED_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "flute_speed");

    private final Map<UUID, Double> charm = new HashMap<>();
    private final Set<UUID> fluteCarriers = new HashSet<>();
    private final Set<UUID> fluteGivenTo = new HashSet<>();
    private final Set<UUID> meleeBonusGiven = new HashSet<>();
    private final Set<UUID> pendingFluteNotification = new HashSet<>();

    private BukkitTask task;

    @Override
    public void enable() {

        task = Bukkit.getScheduler().runTaskTimer(
                LoupGarouPlugin.getInstance(),
                this::tick,
                20L,
                20L
        );

    }

    @Override
    public void disable() {

        if (task != null) {
            task.cancel();
        }

        resetAll();

    }

    public void resetAll() {
        charm.clear();
        fluteCarriers.clear();
        fluteGivenTo.clear();
        meleeBonusGiven.clear();
        pendingFluteNotification.clear();
    }

    public boolean isFluteCarrier(UUID uuid) {
        return fluteCarriers.contains(uuid);
    }

    public boolean hasBeenGivenFlute(UUID uuid) {
        return fluteGivenTo.contains(uuid);
    }

    public void giveFlute(UUID target) {
        fluteCarriers.add(target);
        fluteGivenTo.add(target);
        pendingFluteNotification.add(target);
    }

    public void addMeleeBonus(UUID target) {

        if (meleeBonusGiven.contains(target)) {
            return;
        }

        meleeBonusGiven.add(target);
        charm.merge(target, MELEE_BONUS, Double::sum);
        charm.put(target, Math.min(100.0, charm.get(target)));

    }

    public void onEpisodeChange() {

        for (UUID uuid : pendingFluteNotification) {

            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.sendMessage("§dVous remarquez une étrange Flûte dans votre inventaire... impossible de vous en débarrasser.");
            }

        }

        pendingFluteNotification.clear();

    }

    private void tick() {

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isRevealed() || (game.getState() != GameState.DAY && game.getState() != GameState.NIGHT)) {
            return;
        }

        for (LGPlayer lgSource : game.getPlayers()) {

            if (!lgSource.isAlive()) {
                continue;
            }

            boolean isMain = lgSource.getRole() instanceof JoueurDeFluteRole;
            boolean isCarrier = fluteCarriers.contains(lgSource.getUuid());

            if (!isMain && !isCarrier) {
                continue;
            }

            Player source = Bukkit.getPlayer(lgSource.getUuid());

            if (source == null || !hasFlute(source)) {
                continue;
            }

            double rate = isMain ? MAIN_RATE_PER_SECOND : CARRIER_RATE_PER_SECOND;

            for (LGPlayer lgTarget : game.getPlayers()) {

                if (!lgTarget.isAlive() || lgTarget.getUuid().equals(lgSource.getUuid())) {
                    continue;
                }

                Player target = Bukkit.getPlayer(lgTarget.getUuid());

                if (target == null || !target.getWorld().equals(source.getWorld())) {
                    continue;
                }

                if (target.getLocation().distance(source.getLocation()) > RADIUS) {
                    continue;
                }

                double current = charm.getOrDefault(target.getUniqueId(), 0.0);
                charm.put(target.getUniqueId(), Math.min(100.0, current + rate));

            }

        }

        checkTierBonuses(game);

    }

    private void checkTierBonuses(Game game) {

        long charmedCount = charm.values().stream().filter(value -> value >= 100.0).count();

        long aliveCount = game.getPlayers().stream().filter(LGPlayer::isAlive).count();

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive() || !(lgPlayer.getRole() instanceof JoueurDeFluteRole)) {
                continue;
            }

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player == null) {
                continue;
            }

            JoueurDeFluteRole role = (JoueurDeFluteRole) lgPlayer.getRole();

            if (charmedCount >= 3 && !role.isHeart3Granted()) {
                role.grantHeart3();
                grantHeartBonus(player);
                player.sendMessage("§d3 joueurs charmés ! Vous gagnez 1 cœur de vie permanent.");
            }

            if (charmedCount >= 6 && !role.isStrength6Granted()) {
                role.grantStrength6();
                player.sendMessage("§d6 joueurs charmés ! Vous gagnez Force 0.5.");
            }

            if (charmedCount >= 9 && !role.isStrength9Granted()) {
                role.grantStrength9();
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 0, false, false));
                player.sendMessage("§d9 joueurs charmés ! Vous gagnez Force I.");
            }

            if (charmedCount >= 9 && !role.isSpeed9Granted()) {
                role.grantSpeed9();
                grantSpeedBonus(player);
                player.sendMessage("§dVous gagnez également Speed 0.5.");
            }

            if (charmedCount >= 12 && !role.isHeart12Granted()) {
                role.grantHeart12();
                grantHeartBonus(player);
                player.sendMessage("§d12 joueurs charmés ! Vous gagnez 1 cœur de vie supplémentaire, permanent.");
            }

            if (aliveCount >= 6 && charmedCount >= aliveCount && !role.isResistanceGranted()) {
                role.grantResistance();
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0, false, false));
                player.sendMessage("§dTous les joueurs vivants sont charmés ! Vous gagnez Résistance I.");
            }

        }

    }

    private void grantHeartBonus(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        double existingBonus = maxHealth.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(HEART_KEY))
                .mapToDouble(AttributeModifier::getAmount)
                .findFirst()
                .orElse(0.0);

        maxHealth.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(HEART_KEY))
                .forEach(maxHealth::removeModifier);

        maxHealth.addModifier(new AttributeModifier(HEART_KEY, existingBonus + HEART_BONUS, AttributeModifier.Operation.ADD_NUMBER));

        player.setHealth(Math.min(maxHealth.getValue(), player.getHealth() + HEART_BONUS));

    }

    private void grantSpeedBonus(Player player) {

        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (speed == null) {
            return;
        }

        speed.addModifier(new AttributeModifier(SPEED_KEY, SPEED_BONUS, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

    }

    public void clear(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth != null) {
            maxHealth.getModifiers().stream()
                    .filter(modifier -> modifier.getKey().equals(HEART_KEY))
                    .forEach(maxHealth::removeModifier);
        }

        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (speed != null) {
            speed.getModifiers().stream()
                    .filter(modifier -> modifier.getKey().equals(SPEED_KEY))
                    .forEach(speed::removeModifier);
        }

    }

    private boolean hasFlute(Player player) {

        PlayerInventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {

            if (FluteItem.isFlute(item)) {
                return true;
            }

        }

        return false;

    }

}
