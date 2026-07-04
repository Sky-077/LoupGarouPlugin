package fr.dmall.loupgarou.role.loup;

import java.util.ArrayList;
import java.util.List;

public class PereDesLoupsRole extends WolfRole {

    private boolean infectionAvailable = true;

    public PereDesLoupsRole() {
        super("Père des Loups");
    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Vous recevez Force I chaque nuit, comme un Loup-Garou.");
        lines.add("Une fois par partie, dans la minute qui suit un coup mortel porté à votre propre victime,");
        lines.add("vous pouvez l'infecter avec /lg infecter <joueur> pour la transformer en Loup-Garou au lieu de la tuer.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

    public boolean isInfectionAvailable() {
        return infectionAvailable;
    }

    public void consumeInfection() {
        infectionAvailable = false;
    }

}
