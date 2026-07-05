package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class VilainPetitLoupManager {

    private static final double SPEED_BONUS = 0.1; // moitié de Speed I (~0.2)

    private static final NamespacedKey SPEED_KEY = new NamespacedKey(LoupGarouPlugin.getInstance(), "vilain_petit_loup_speed");

    private VilainPetitLoupManager() {
    }

    public static void applyNightSpeed(Player player) {

        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (speed == null || speed.getModifiers().stream().anyMatch(modifier -> modifier.getKey().equals(SPEED_KEY))) {
            return;
        }

        speed.addModifier(new AttributeModifier(SPEED_KEY, SPEED_BONUS, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

    }

    public static void clear(Player player) {

        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);

        if (speed == null) {
            return;
        }

        speed.getModifiers().stream()
                .filter(modifier -> modifier.getKey().equals(SPEED_KEY))
                .forEach(speed::removeModifier);

    }

}
