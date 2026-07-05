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
        lines.add("Vous disposez en permanence d'un bonus de dégâts au corps-à-corps équivalent à Force 0.5 (+1.5 dégâts), jour et nuit.");
        lines.add("La nuit, vous bénéficiez en plus d'un bonus de vitesse de déplacement (Speed 0.5).");
        lines.add("Éliminez les villageois en combat direct (PVP libre, pas de vote ni de ciblage).");
        lines.add("En restant à proximité d'un joueur, vous le corrompez (1% toutes les 5 secondes).");
        lines.add("Si un joueur corrompu à 100% meurt de la main d'un loup, le Père des Loups peut choisir de l'infecter.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
