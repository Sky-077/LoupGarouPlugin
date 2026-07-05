package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.solo.AngeRole;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AngeManager {

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "ange_hearts");
    private static final double BASE_HEARTS = 10.0;
    private static final double LOW_HEALTH_THRESHOLD = 8.0; // 4 cœurs

    private AngeManager() {
    }

    public static void applyHearts(Player player, double hearts) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        removeModifier(maxHealth);

        double amount = (hearts - BASE_HEARTS) * 2.0;

        if (amount != 0.0) {
            maxHealth.addModifier(new AttributeModifier(MODIFIER_KEY, amount, AttributeModifier.Operation.ADD_NUMBER));
        }

        player.setHealth(maxHealth.getValue());

    }

    public static void clearHearts(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        removeModifier(maxHealth);

        if (player.getHealth() > maxHealth.getValue()) {
            player.setHealth(maxHealth.getValue());
        }

        player.removePotionEffect(PotionEffectType.WEAKNESS);

    }

    public static boolean isBelowLowHealthThreshold(Player player) {
        return player.getHealth() < LOW_HEALTH_THRESHOLD;
    }

    public static void onDeath(Game game, LGPlayer deceased, Player killer) {

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive() || !(lgPlayer.getRole() instanceof AngeRole)) {
                continue;
            }

            AngeRole ange = (AngeRole) lgPlayer.getRole();
            Player angePlayer = Bukkit.getPlayer(lgPlayer.getUuid());

            if (angePlayer == null) {
                continue;
            }

            if (ange.isTarget(deceased.getUuid()) && !ange.isConditionFulfilled()
                    && killer != null && killer.getUniqueId().equals(lgPlayer.getUuid())) {

                ange.fulfillCondition();
                applyHearts(angePlayer, 15);
                angePlayer.sendMessage("§bVous avez éliminé votre cible ! Vous passez à 15 cœurs.");

            }

            if (ange.isProtege(deceased.getUuid()) && !ange.isProtegeDead()) {

                ange.markProtegeDead();
                lgPlayer.setTeamOverride(null);
                applyHearts(angePlayer, 12);
                angePlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0, false, false));
                angePlayer.sendMessage("§cVotre protégé est mort... Vous passez à 12 cœurs et devez désormais gagner seul.");

            }

        }

    }

    private static void removeModifier(AttributeInstance maxHealth) {

        maxHealth.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(MODIFIER_KEY))
                .forEach(maxHealth::removeModifier);

    }

}
