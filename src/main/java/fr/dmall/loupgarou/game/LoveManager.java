package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleTeam;
import fr.dmall.loupgarou.role.village.VillageoisRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LoveManager implements Manager {

    private UUID loverA;
    private UUID loverB;
    private UUID cupidon;

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
        reset();
    }

    public void reset() {
        loverA = null;
        loverB = null;
        cupidon = null;
    }

    public void link(LGPlayer a, LGPlayer b, LGPlayer cupidonPlayer) {

        loverA = a.getUuid();
        loverB = b.getUuid();
        cupidon = cupidonPlayer.getUuid();

        a.setTeamOverride(RoleTeam.AMOUREUX);
        b.setTeamOverride(RoleTeam.AMOUREUX);
        cupidonPlayer.setTeamOverride(RoleTeam.AMOUREUX);

    }

    public boolean isCupidon(UUID uuid) {
        return uuid != null && uuid.equals(cupidon);
    }

    public void replaceCupidon(UUID newCupidon) {
        this.cupidon = newCupidon;
    }

    public UUID getPartner(UUID uuid) {

        if (uuid.equals(loverA)) {
            return loverB;
        }

        if (uuid.equals(loverB)) {
            return loverA;
        }

        return null;

    }

    public void handleDeath(Player deceased) {

        UUID partnerUuid = getPartner(deceased.getUniqueId());

        if (partnerUuid == null) {
            return;
        }

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        Player partner = Bukkit.getPlayer(partnerUuid);
        LGPlayer lgPartner = findByUuid(playerManager, partnerUuid);

        if (lgPartner != null && lgPartner.isAlive() && partner != null) {

            Bukkit.broadcastMessage("§d💔 " + partner.getName() + " meurt de chagrin après la mort de son âme sœur !");

            DeathManager deathManager = LoupGarouPlugin.getInstance()
                    .getManagerRegistry()
                    .getManager(DeathManager.class);

            deathManager.killInstantly(partner, null);

        }

        tryRevertCupidon(playerManager);

    }

    private void tryRevertCupidon(PlayerManager playerManager) {

        if (cupidon == null) {
            return;
        }

        LGPlayer lgA = findByUuid(playerManager, loverA);
        LGPlayer lgB = findByUuid(playerManager, loverB);

        boolean aDead = lgA == null || !lgA.isAlive();
        boolean bDead = lgB == null || !lgB.isAlive();

        if (!aDead || !bDead) {
            return;
        }

        Player cupidonPlayer = Bukkit.getPlayer(cupidon);
        LGPlayer lgCupidon = findByUuid(playerManager, cupidon);

        if (lgCupidon != null && lgCupidon.isAlive()) {

            lgCupidon.setTeamOverride(null);
            lgCupidon.setRole(new VillageoisRole());

            if (cupidonPlayer != null) {
                cupidonPlayer.sendMessage("§7Vos amoureux sont tous les deux morts... Vous redevenez un simple Villageois.");
            }

        }

        cupidon = null;

    }

    private LGPlayer findByUuid(PlayerManager playerManager, UUID uuid) {

        if (uuid == null) {
            return null;
        }

        for (LGPlayer lgPlayer : playerManager.getPlayers()) {

            if (lgPlayer.getUuid().equals(uuid)) {
                return lgPlayer;
            }

        }

        return null;

    }

}
