package fr.dmall.loupgarou.role.loup;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LoupGarouCraintifRole extends WolfRole {

    public LoupGarouCraintifRole() {
        super("Loup-Garou Craintif");
    }

    @Override
    public void onDay(Player player) {
        // Les effets dépendent du nombre de loups à proximité, gérés dynamiquement par CraintifManager.
    }

    @Override
    public void onNight(Player player) {
        // Les effets dépendent du nombre de loups à proximité, gérés dynamiquement par CraintifManager.
    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Aucune procédure de vote ou de ciblage : vous éliminez le Village directement au corps-à-corps, en PVP libre.");
        lines.add("Rester proche d'un joueur le corrompt progressivement : +1% toutes les 5 secondes.");
        lines.add("Votre état varie selon le nombre de Loups-Garous présents dans un rayon de 20 blocs autour de vous (vous y compris) :");
        lines.add("  - Au-delà de 4 loups à portée : Faiblesse I.");
        lines.add("  - 2 loups ou moins à portée : Résistance I de jour, Force I de nuit.");
        lines.add("  - Si vous êtes l'unique loup présent : Speed 0.5 s'ajoute à l'effet ci-dessus.");
        lines.add("Votre vote est toujours neutralisé en blanc, et votre mort reste silencieuse : ni la Sorcière ni le Père des Loups ne peuvent intervenir sur votre sort.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
