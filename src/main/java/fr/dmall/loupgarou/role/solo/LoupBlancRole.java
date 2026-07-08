package fr.dmall.loupgarou.role.solo;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoupBlancRole extends Role {

    private final List<UUID> knownWolves = new ArrayList<>();

    public LoupBlancRole() {
        super("Loup Blanc", RoleTeam.NEUTRAL);
    }

    public void setKnownWolves(List<UUID> wolves) {
        knownWolves.clear();
        knownWolves.addAll(wolves);
    }

    @Override
    public void onNight(Player player) {

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                PotionEffect.INFINITE_DURATION,
                0,
                false,
                false
        ));

    }

    @Override
    public void onDay(Player player) {
        player.removePotionEffect(PotionEffectType.STRENGTH);
    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Rôle solitaire : la victoire n'est acquise qu'en étant l'ultime survivant, Loups compris.");
        lines.add("À la manière d'un Loup, vous obtenez Force I chaque nuit.");
        lines.add("La révélation des rôles vous porte à 15 cœurs de vie.");
        lines.add("L'identité des Loups-Garous vous est connue, mais gagner exige de tous les éliminer, eux inclus.");

        if (knownWolves.isEmpty()) {
            lines.add("§fCette partie ne compte aucun Loup-Garou.");
        } else {

            lines.add("§eLoups identifiés :");

            for (UUID wolf : knownWolves) {

                String name = Bukkit.getOfflinePlayer(wolf).getName();

                if (name == null) {
                    name = "Inconnu";
                }

                lines.add(" - " + name);

            }

        }

        return lines.toArray(new String[0]);

    }

}
