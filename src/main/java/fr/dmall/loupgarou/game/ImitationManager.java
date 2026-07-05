package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import fr.dmall.loupgarou.role.loup.WolfRole;
import fr.dmall.loupgarou.role.solo.ChasseurDePrimesRole;
import fr.dmall.loupgarou.role.solo.FeuFolletRole;
import fr.dmall.loupgarou.role.solo.LoupBlancRole;
import fr.dmall.loupgarou.role.village.CupidonRole;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImitationManager {

    private ImitationManager() {
    }

    public static void imitate(Game game, LGPlayer imitatorLg, Player imitator, LGPlayer victimLg, Role victimRole) {

        if (victimRole == null) {
            return;
        }

        Role newRole;

        try {
            newRole = victimRole.getClass().getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            return;
        }

        imitator.removePotionEffect(PotionEffectType.STRENGTH);

        imitatorLg.setRole(newRole);

        handleSpecialCases(game, imitatorLg, imitator, victimLg, newRole);

        newRole.sendInstructions(imitator);
        GameStarter.giveRoleItems(imitatorLg, imitator);

        if (game.getState() == GameState.NIGHT) {
            newRole.onNight(imitator);
        } else {
            newRole.onDay(imitator);
        }

        imitator.sendMessage("§dVous avez imité le rôle de votre victime : §6" + newRole.getName() + "§d !");

    }

    private static void handleSpecialCases(Game game, LGPlayer imitatorLg, Player imitator, LGPlayer victimLg, Role newRole) {

        if (newRole instanceof CupidonRole) {

            LoveManager loveManager = LoupGarouPlugin.getInstance()
                    .getManagerRegistry()
                    .getManager(LoveManager.class);

            if (loveManager.isCupidon(victimLg.getUuid())) {

                ((CupidonRole) newRole).consumePower();
                loveManager.replaceCupidon(imitatorLg.getUuid());
                imitatorLg.setTeamOverride(RoleTeam.AMOUREUX);

                imitator.sendMessage("§dVous héritez du lien amoureux de votre victime ! Vous rejoignez le camp des Amoureux.");

            }

            return;

        }

        if (newRole instanceof FeuFolletRole) {
            ((FeuFolletRole) newRole).checkInitialInvisibility(imitator);
            return;
        }

        if (newRole instanceof LoupBlancRole) {

            List<UUID> wolfUuids = new ArrayList<>();

            for (LGPlayer lgPlayer : game.getPlayers()) {

                if (lgPlayer.isAlive() && lgPlayer.getRole() instanceof WolfRole) {
                    wolfUuids.add(lgPlayer.getUuid());
                }

            }

            ((LoupBlancRole) newRole).setKnownWolves(wolfUuids);
            LoupBlancManager.applyHearts(imitator);

            return;

        }

        if (newRole instanceof WolfRole) {

            List<LGPlayer> otherWolves = new ArrayList<>();

            for (LGPlayer lgPlayer : game.getPlayers()) {

                if (lgPlayer.isAlive() && lgPlayer.getRole() instanceof WolfRole && !lgPlayer.getUuid().equals(imitatorLg.getUuid())) {
                    otherWolves.add(lgPlayer);
                }

            }

            List<UUID> otherWolfUuids = new ArrayList<>();

            for (LGPlayer wolf : otherWolves) {
                otherWolfUuids.add(wolf.getUuid());
                ((WolfRole) wolf.getRole()).addKnownWolf(imitatorLg.getUuid());
            }

            ((WolfRole) newRole).setKnownWolves(otherWolfUuids);

            imitatorLg.setTeamOverride(RoleTeam.NEUTRAL);

            return;

        }

        if (newRole instanceof ChasseurDePrimesRole) {
            BountyManager.issueFirstContract(game, imitatorLg);
        }

        if (newRole.getTeam() != RoleTeam.NEUTRAL) {
            imitatorLg.setTeamOverride(RoleTeam.NEUTRAL);
        }

    }

}
