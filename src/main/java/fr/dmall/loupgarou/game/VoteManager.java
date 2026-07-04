package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class VoteManager implements Manager {

    private static final int ROUND_COUNT = 3;

    private static final List<String> ALL_ROLE_NAMES = List.of(
            "Villageois", "Loup-Garou", "Père des Loups", "Petite Fille",
            "Voyante", "Sorcière", "Chasseur", "Cupidon"
    );

    private boolean active;
    private int startEpisode;
    private final Map<UUID, UUID> votes = new HashMap<>();
    private final Set<UUID> passed = new HashSet<>();

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
        reset();
    }

    public void reset() {
        active = false;
        startEpisode = 0;
        votes.clear();
        passed.clear();
    }

    public boolean isActive() {
        return active;
    }

    public void startVoting(Game game) {

        active = true;
        startEpisode = game.getEpisode();
        votes.clear();
        passed.clear();

        Bukkit.broadcastMessage("§6Le vote est désormais ouvert ! Rendez-vous dans une maison de vote pour voter.");

    }

    public void castVote(Player voter, UUID target) {
        passed.remove(voter.getUniqueId());
        votes.put(voter.getUniqueId(), target);
    }

    public void castPass(Player voter) {
        votes.remove(voter.getUniqueId());
        passed.add(voter.getUniqueId());
    }

    public void onEpisodeChange(Game game) {

        if (!active) {
            return;
        }

        resolveRound(game);

        int roundsElapsed = game.getEpisode() - startEpisode;

        votes.clear();
        passed.clear();

        if (roundsElapsed >= ROUND_COUNT) {
            active = false;
            Bukkit.broadcastMessage("§7La période de vote est terminée.");
        }

    }

    private void resolveRound(Game game) {

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        Map<UUID, Integer> tally = new HashMap<>();

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (!lgPlayer.isAlive()) {
                continue;
            }

            RoleTeam team = lgPlayer.getEffectiveTeam();

            if (team != RoleTeam.VILLAGE && team != RoleTeam.LOUP) {
                continue;
            }

            Player player = Bukkit.getPlayer(lgPlayer.getUuid());

            if (player == null) {
                continue;
            }

            UUID target = votes.get(lgPlayer.getUuid());

            if (target != null) {
                HonorManager.gainHonor(lgPlayer, player);
                tally.merge(target, 1, Integer::sum);
            } else if (!passed.contains(lgPlayer.getUuid())) {
                HonorManager.loseHonor(lgPlayer, player);
            }

        }

        announceMostVoted(playerManager, tally);

    }

    private void announceMostVoted(PlayerManager playerManager, Map<UUID, Integer> tally) {

        if (tally.isEmpty()) {
            return;
        }

        int topCount = Collections.max(tally.values());

        List<UUID> topCandidates = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : tally.entrySet()) {

            if (entry.getValue() == topCount) {
                topCandidates.add(entry.getKey());
            }

        }

        Collections.shuffle(topCandidates);
        UUID topTarget = topCandidates.get(0);

        LGPlayer lgTarget = null;

        for (LGPlayer lgPlayer : playerManager.getPlayers()) {

            if (lgPlayer.getUuid().equals(topTarget)) {
                lgTarget = lgPlayer;
                break;
            }

        }

        if (lgTarget == null || lgTarget.getRole() == null) {
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(topTarget);
        String targetName = (targetPlayer != null) ? targetPlayer.getName() : "un joueur";

        List<String> decoyPool = new ArrayList<>(ALL_ROLE_NAMES);
        decoyPool.remove(lgTarget.getRole().getName());
        Collections.shuffle(decoyPool);

        List<String> revealed = new ArrayList<>();
        revealed.add(lgTarget.getRole().getName());
        revealed.add(decoyPool.get(0));
        revealed.add(decoyPool.get(1));
        Collections.shuffle(revealed);

        Bukkit.broadcastMessage("§d[Vote] " + targetName + " a été le plus désigné par le village ! "
                + "§7Rôle possible : §f" + String.join("§7, §f", revealed) + " §7(un seul est le vrai)");

    }

}
