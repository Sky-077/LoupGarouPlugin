package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class LoupBlancManager {

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "loup_blanc_hearts");
    private static final double BONUS_HEALTH = 10.0; // +5 cœurs (15 au total)

    private LoupBlancManager() {
    }

    public static void applyHearts(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        maxHealth.addModifier(new AttributeModifier(MODIFIER_KEY, BONUS_HEALTH, AttributeModifier.Operation.ADD_NUMBER));
        player.setHealth(maxHealth.getValue());

    }

    public static void clearHearts(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        maxHealth.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(MODIFIER_KEY))
                .forEach(maxHealth::removeModifier);

        if (player.getHealth() > maxHealth.getValue()) {
            player.setHealth(maxHealth.getValue());
        }

    }

}
