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

    private static final NamespacedKey HEART_MODIFIER_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "honor_hearts");
    private static final NamespacedKey SPEED_MODIFIER_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "honor_speed");
    private static final double HEART_AMOUNT = 2.0;
    private static final double SPEED_BONUS = 0.1; // moitié de Speed I (~0.2), même convention que CraintifManager/VilainPetitLoupManager

    private HonorManager() {
    }

    public static void gainHonor(LGPlayer lgPlayer, Player player) {

        lgPlayer.setHonor(lgPlayer.getHonor() + 1);
        applyEffects(lgPlayer, player);

    }

    public static void loseHonor(LGPlayer lgPlayer, Player player) {

        lgPlayer.setHonor(lgPlayer.getHonor() - 1);
        applyEffects(lgPlayer, player);

    }

    // Trahison/tir ami : pousse toujours l'honneur vers l'extrême défavorable au camp du tueur, jamais
    // vers l'extrême qui l'avantagerait (un Loup qui tue un autre Loup ne doit pas se rapprocher du -3
    // qui récompense les Loups — il doit au contraire se rapprocher du +3 qui les pénalise).
    public static void applyBetrayalPenalty(LGPlayer lgPlayer, Player player) {

        if (lgPlayer.getEffectiveTeam() == RoleTeam.LOUP) {
            gainHonor(lgPlayer, player);
        } else {
            loseHonor(lgPlayer, player);
        }

    }

    public static void setHonor(LGPlayer lgPlayer, Player player, int honor) {

        lgPlayer.setHonor(honor);
        applyEffects(lgPlayer, player);

    }

    public static void clearModifier(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth != null) {
            removeExistingModifier(maxHealth, HEART_MODIFIER_KEY);
            clampHealth(player, maxHealth);
        }

        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (speed != null) {
            removeExistingModifier(speed, SPEED_MODIFIER_KEY);
        }

    }

    private static void applyEffects(LGPlayer lgPlayer, Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth != null) {

            removeExistingModifier(maxHealth, HEART_MODIFIER_KEY);

            double heartAmount = getHeartAmount(lgPlayer);

            if (heartAmount != 0.0) {
                maxHealth.addModifier(new AttributeModifier(HEART_MODIFIER_KEY, heartAmount, AttributeModifier.Operation.ADD_NUMBER));
            }

            clampHealth(player, maxHealth);

        }

        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (speed != null) {

            removeExistingModifier(speed, SPEED_MODIFIER_KEY);

            double speedAmount = getSpeedAmount(lgPlayer);

            if (speedAmount != 0.0) {
                speed.addModifier(new AttributeModifier(SPEED_MODIFIER_KEY, speedAmount, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            }

        }

    }

    private static double getHeartAmount(LGPlayer lgPlayer) {

        RoleTeam team = lgPlayer.getEffectiveTeam();
        int honor = lgPlayer.getHonor();

        if (team == RoleTeam.VILLAGE) {

            if (honor >= 3) {
                return HEART_AMOUNT;
            }

            if (honor <= -3) {
                return -HEART_AMOUNT;
            }

        } else if (team == RoleTeam.LOUP) {

            if (honor >= 3) {
                return -HEART_AMOUNT;
            }

            if (honor <= -3) {
                return HEART_AMOUNT;
            }

        }

        return 0.0;

    }

    private static double getSpeedAmount(LGPlayer lgPlayer) {

        RoleTeam team = lgPlayer.getEffectiveTeam();
        int honor = lgPlayer.getHonor();

        if (team == RoleTeam.VILLAGE) {

            if (honor >= 2) {
                return SPEED_BONUS;
            }

            if (honor <= -2) {
                return -SPEED_BONUS;
            }

        } else if (team == RoleTeam.LOUP) {

            if (honor >= 2) {
                return -SPEED_BONUS;
            }

            if (honor <= -2) {
                return SPEED_BONUS;
            }

        }

        return 0.0;

    }

    private static void removeExistingModifier(AttributeInstance attribute, NamespacedKey key) {

        attribute.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(key))
                .forEach(attribute::removeModifier);

    }

    private static void clampHealth(Player player, AttributeInstance maxHealth) {

        double max = maxHealth.getValue();

        if (player.getHealth() > max) {
            player.setHealth(max);
        }

    }

}
