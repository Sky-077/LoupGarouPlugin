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
        lines.add("Vous êtes solitaire : vous gagnez seul en étant l'unique survivant, même face aux Loups.");
        lines.add("Vous recevez la Force I chaque nuit, comme un loup.");
        lines.add("Vous obtenez 15 cœurs de vie à la révélation des rôles.");
        lines.add("Vous connaissez l'identité des Loups-Garous, mais vous devrez tous les éliminer (eux comme les autres) pour gagner.");

        if (knownWolves.isEmpty()) {
            lines.add("§fAucun Loup-Garou dans cette partie.");
        } else {

            lines.add("§eLoups connus :");

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
