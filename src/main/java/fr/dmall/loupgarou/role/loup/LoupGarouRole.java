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
        lines.add("La nuit venue, vous bénéficiez de Force I.");
        lines.add("Aucune procédure de vote ou de ciblage : vous éliminez le Village directement au corps-à-corps, en PVP libre.");
        lines.add("Rester proche d'un joueur le corrompt progressivement : +1% toutes les 5 secondes.");
        lines.add("Quand une victime corrompue à 100% meurt sous les coups d'un Loup, le Père des Loups a la possibilité de la convertir plutôt que de la laisser mourir.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
