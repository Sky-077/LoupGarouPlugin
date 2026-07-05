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
        lines.add("Vous recevez Force I chaque nuit, comme un Loup-Garou.");
        lines.add("En restant à proximité d'un joueur, vous le corrompez bien plus vite qu'un Loup-Garou classique (1% par seconde).");
        lines.add("Quand un joueur corrompu à 100% meurt de la main d'un loup, vous recevez un message cliquable (10s) :");
        lines.add("§a/lg infecter <joueur> §7pour le transformer en Loup-Garou, ou §c/lg laissermourir <joueur> §7pour le laisser mourir.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
