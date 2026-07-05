package fr.dmall.loupgarou.role.loup;

import java.util.ArrayList;
import java.util.List;

public class LoupGarouRole extends WolfRole {

    public LoupGarouRole() {
        super("Loup-Garou");
    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Vous recevez Force I chaque nuit.");
        lines.add("Éliminez les villageois en combat direct (PVP libre, pas de vote ni de ciblage).");
        lines.add("En restant à proximité d'un joueur, vous le corrompez (1% toutes les 5 secondes).");
        lines.add("Si un joueur corrompu à 100% meurt de la main d'un loup, le Père des Loups peut choisir de l'infecter.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
