package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class HonorManager {

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "honor_hearts");
    private static final double HEART_AMOUNT = 2.0;

    private HonorManager() {
    }

    public static void gainHonor(LGPlayer lgPlayer, Player player) {

        lgPlayer.setHonor(lgPlayer.getHonor() + 1);
        applyHeartEffect(lgPlayer, player);

    }

    public static void loseHonor(LGPlayer lgPlayer, Player player) {

        lgPlayer.setHonor(lgPlayer.getHonor() - 1);
        applyHeartEffect(lgPlayer, player);

    }

    public static void setHonor(LGPlayer lgPlayer, Player player, int honor) {

        lgPlayer.setHonor(honor);
        applyHeartEffect(lgPlayer, player);

    }

    public static void clearModifier(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        removeExistingModifier(maxHealth);
        clampHealth(player, maxHealth);

    }

    private static void applyHeartEffect(LGPlayer lgPlayer, Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        removeExistingModifier(maxHealth);

        double amount = getHeartAmount(lgPlayer);

        if (amount != 0.0) {
            maxHealth.addModifier(new AttributeModifier(MODIFIER_KEY, amount, AttributeModifier.Operation.ADD_NUMBER));
        }

        clampHealth(player, maxHealth);

    }

    private static double getHeartAmount(LGPlayer lgPlayer) {

        RoleTeam team = lgPlayer.getEffectiveTeam();
        int honor = lgPlayer.getHonor();

        if (team == RoleTeam.VILLAGE) {

            if (honor == 3) {
                return HEART_AMOUNT;
            }

            if (honor == -3) {
                return -HEART_AMOUNT;
            }

        } else if (team == RoleTeam.LOUP) {

            if (honor == 3) {
                return -HEART_AMOUNT;
            }

            if (honor == -3) {
                return HEART_AMOUNT;
            }

        }

        return 0.0;

    }

    private static void removeExistingModifier(AttributeInstance maxHealth) {

        maxHealth.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(MODIFIER_KEY))
                .forEach(maxHealth::removeModifier);

    }

    private static void clampHealth(Player player, AttributeInstance maxHealth) {

        double max = maxHealth.getValue();

        if (player.getHealth() > max) {
            player.setHealth(max);
        }

    }

}
