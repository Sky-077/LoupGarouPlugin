package fr.dmall.loupgarou.role.loup;

import fr.dmall.loupgarou.game.VilainPetitLoupManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VilainPetitLoupRole extends WolfRole {

    public VilainPetitLoupRole() {
        super("Vilain Petit Loup");
    }

    @Override
    public void onNight(Player player) {
        VilainPetitLoupManager.applyNightSpeed(player);
    }

    @Override
    public void onDay(Player player) {
        VilainPetitLoupManager.clear(player);
    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("En permanence, jour et nuit, vos attaques au corps-à-corps bénéficient d'un bonus de dégâts équivalent à Force 0.5 (+1.5 dégâts).");
        lines.add("Un bonus de vitesse supplémentaire (Speed 0.5) s'ajoute à cela durant la nuit.");
        lines.add("Aucune procédure de vote ou de ciblage : vous éliminez le Village directement au corps-à-corps, en PVP libre.");
        lines.add("Rester proche d'un joueur le corrompt progressivement : +1% toutes les 5 secondes.");
        lines.add("Quand une victime corrompue à 100% meurt sous les coups d'un Loup, le Père des Loups a la possibilité de la convertir plutôt que de la laisser mourir.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
