package fr.dmall.loupgarou.role.loup;

import java.util.ArrayList;
import java.util.List;

public class PereDesLoupsRole extends WolfRole {

    public PereDesLoupsRole() {
        super("Père des Loups");
    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Comme un Loup-Garou, vous bénéficiez de Force I chaque nuit.");
        lines.add("Votre proximité corrompt les joueurs à un rythme bien plus élevé qu'un Loup-Garou ordinaire : +1% chaque seconde.");
        lines.add("Lorsqu'une victime corrompue à 100% succombe sous les coups d'un Loup, un message cliquable vous est envoyé, valable 10 secondes :");
        lines.add("§a/lg infecter <joueur> §fla convertit en Loup-Garou, §c/lg laissermourir <joueur> §fla laisse mourir.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
