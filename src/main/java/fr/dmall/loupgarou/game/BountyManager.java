package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.solo.ChasseurDePrimesRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BountyManager {

    private BountyManager() {
    }

    public static void onPvpEnabled(Game game) {

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive() || !(lgPlayer.getRole() instanceof ChasseurDePrimesRole)) {
                continue;
            }

            issueContract(game, lgPlayer, (ChasseurDePrimesRole) lgPlayer.getRole());

        }

    }

    public static void onEpisodeChange(Game game) {

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive() || !(lgPlayer.getRole() instanceof ChasseurDePrimesRole)) {
                continue;
            }

            ChasseurDePrimesRole role = (ChasseurDePrimesRole) lgPlayer.getRole();

            if (role.isSecondContractDue(game.getEpisode())) {
                issueContract(game, lgPlayer, role);
            }

        }

    }

    public static void issueFirstContract(Game game, LGPlayer hunterLgPlayer) {

        if (!(hunterLgPlayer.getRole() instanceof ChasseurDePrimesRole)) {
            return;
        }

        ChasseurDePrimesRole role = (ChasseurDePrimesRole) hunterLgPlayer.getRole();

        if (role.getContractsIssued() > 0) {
            return;
        }

        issueContract(game, hunterLgPlayer, role);

    }

    private static void issueContract(Game game, LGPlayer hunterLgPlayer, ChasseurDePrimesRole role) {

        List<UUID> candidates = new ArrayList<>();

        for (LGPlayer other : game.getPlayers()) {

            if (other.getUuid().equals(hunterLgPlayer.getUuid()) || !other.isAlive()) {
                continue;
            }

            if (role.hasContractOn(other.getUuid())) {
                continue;
            }

            candidates.add(other.getUuid());

        }

        if (candidates.isEmpty()) {
            return;
        }

        Collections.shuffle(candidates);
        UUID target = candidates.get(0);

        role.issueContract(target);

        Player hunter = Bukkit.getPlayer(hunterLgPlayer.getUuid());

        if (hunter != null) {
            String targetName = Bukkit.getOfflinePlayer(target).getName();
            hunter.sendMessage("§6Nouveau contrat : éliminez " + targetName + " !");
        }

    }

}
