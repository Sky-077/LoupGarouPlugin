package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class PoisonManager {

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "poison_hearts");
    private static final double HEARTS_REMOVED = 4.0; // 2 cœurs

    private PoisonManager() {
    }

    public static void applyPoison(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        maxHealth.addModifier(new AttributeModifier(MODIFIER_KEY, -HEARTS_REMOVED, AttributeModifier.Operation.ADD_NUMBER));

        if (player.getHealth() > maxHealth.getValue()) {
            player.setHealth(maxHealth.getValue());
        }

    }

    public static void clearPoison(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        maxHealth.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(MODIFIER_KEY))
                .forEach(maxHealth::removeModifier);

    }

}
