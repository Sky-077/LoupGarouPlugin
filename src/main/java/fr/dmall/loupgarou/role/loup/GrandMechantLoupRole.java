package fr.dmall.loupgarou.role.loup;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class GrandMechantLoupRole extends WolfRole {

    public GrandMechantLoupRole() {
        super("Grand Méchant Loup");
    }

    @Override
    public void onDay(Player player) {

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                PotionEffect.INFINITE_DURATION,
                0,
                false,
                false
        ));

    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Vous recevez Force I en permanence, jour et nuit (contrairement aux autres loups).");
        lines.add("Éliminez les villageois en combat direct (PVP libre, pas de vote ni de ciblage).");
        lines.add("En restant à proximité d'un joueur, vous le corrompez (1% toutes les 5 secondes).");
        lines.add("Si un joueur corrompu à 100% meurt de la main d'un loup, le Père des Loups peut choisir de l'infecter.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
