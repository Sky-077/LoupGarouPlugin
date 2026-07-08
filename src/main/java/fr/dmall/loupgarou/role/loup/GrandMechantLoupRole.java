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
        lines.add("Contrairement aux autres Loups, votre Force I reste active à toute heure, jour comme nuit.");
        lines.add("Aucune procédure de vote ou de ciblage : vous éliminez le Village directement au corps-à-corps, en PVP libre.");
        lines.add("Rester proche d'un joueur le corrompt progressivement : +1% toutes les 5 secondes.");
        lines.add("Quand une victime corrompue à 100% meurt sous les coups d'un Loup, le Père des Loups a la possibilité de la convertir plutôt que de la laisser mourir.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
