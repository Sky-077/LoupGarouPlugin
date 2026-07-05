package fr.dmall.loupgarou.role;

import org.bukkit.entity.Player;

public interface NightInvisibilityRole {

    boolean hasNoArmor(Player player);

    void tryActivateInvisibility(Player player);

    void removeInvisibility(Player player);

}
