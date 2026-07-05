package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class BienfaiteurManager {

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "bienfaiteur_hearts");
    private static final double HEART_AMOUNT = 2.0;

    private BienfaiteurManager() {
    }

    public static void grantHeart(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        double existingBonus = maxHealth.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(MODIFIER_KEY))
                .mapToDouble(AttributeModifier::getAmount)
                .findFirst()
                .orElse(0.0);

        removeExistingModifier(maxHealth);

        maxHealth.addModifier(new AttributeModifier(MODIFIER_KEY, existingBonus + HEART_AMOUNT, AttributeModifier.Operation.ADD_NUMBER));

        player.setHealth(Math.min(maxHealth.getValue(), player.getHealth() + HEART_AMOUNT));

    }

    public static void clear(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        removeExistingModifier(maxHealth);

    }

    private static void removeExistingModifier(AttributeInstance maxHealth) {

        maxHealth.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(MODIFIER_KEY))
                .forEach(maxHealth::removeModifier);

    }

}
