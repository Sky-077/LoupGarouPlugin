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
        lines.add("Éliminez les villageois en combat direct (PVP libre, pas de vote ni de ciblage).");
        lines.add("En restant à proximité d'un joueur, vous le corrompez (1% toutes les 5 secondes).");
        lines.add("Vos effets dépendent du nombre de Loups-Garous à moins de 20 blocs de vous (vous inclus) :");
        lines.add("  - Plus de 4 loups : Faiblesse I.");
        lines.add("  - 2 loups ou moins : Résistance I le jour, Force I la nuit.");
        lines.add("  - Seul (vous êtes l'unique loup à portée) : Speed 0.5, en plus de l'effet précédent.");
        lines.add("Vous ne pouvez voter que blanc, et votre mort ne produit aucun message : vous ne pouvez être ni sauvé par la Sorcière, ni infecté par le Père des Loups.");
        lines.addAll(getWolfPackLines());

        return lines.toArray(new String[0]);

    }

}
