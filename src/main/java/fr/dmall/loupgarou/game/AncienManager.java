package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class AncienManager {

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "ancien_killer_penalty");

    private AncienManager() {
    }

    public static void halveMaxHealth(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        double currentHearts = maxHealth.getValue() / 2.0;
        double newHearts = Math.ceil(currentHearts / 2.0);
        double delta = (newHearts * 2.0) - maxHealth.getValue();

        if (delta != 0.0) {
            maxHealth.addModifier(new AttributeModifier(MODIFIER_KEY, delta, AttributeModifier.Operation.ADD_NUMBER));
        }

        if (player.getHealth() > maxHealth.getValue()) {
            player.setHealth(maxHealth.getValue());
        }

    }

    public static void clear(Player player) {

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        maxHealth.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(MODIFIER_KEY))
                .forEach(maxHealth::removeModifier);

    }

}
