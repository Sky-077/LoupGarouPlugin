package fr.dmall.loupgarou.game;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleFactory;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class VoteManager implements Manager {

    private static final int ROUND_COUNT = 3;

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
            Bukkit.broadcastMessage("§fLa période de vote est terminée.");
        } else {
            Bukkit.broadcastMessage("§6Nouvel épisode de vote ! Rendez-vous dans une maison de vote pour voter à nouveau.");
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

        announceMostVoted(game, playerManager, tally);

    }

    private void announceMostVoted(Game game, PlayerManager playerManager, Map<UUID, Integer> tally) {

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

        List<String> revealed = new ArrayList<>();
        revealed.add(lgTarget.getRole().getName());
        revealed.addAll(pickDecoys(game, lgTarget));
        Collections.shuffle(revealed);

        Bukkit.broadcastMessage("§d[Vote] " + targetName + " a été le plus désigné par le village ! "
                + "§fRôle possible : §e" + String.join("§f, §e", revealed) + " §f(un seul est le vrai)");

    }

    private List<String> pickDecoys(Game game, LGPlayer target) {

        Map<String, RoleTeam> roleTeams = new LinkedHashMap<>();

        for (String key : RoleFactory.getRegisteredNames()) {
            Role role = RoleFactory.create(key);
            roleTeams.put(role.getName(), role.getTeam());
        }

        String targetName = target.getRole().getName();
        RoleTeam targetTeam = target.getRole().getTeam();

        Set<String> inPlay = new LinkedHashSet<>();

        for (LGPlayer lgPlayer : game.getPlayers()) {

            if (lgPlayer.getRole() != null && !lgPlayer.getRole().getName().equals(targetName)) {
                inPlay.add(lgPlayer.getRole().getName());
            }

        }

        List<String> decoys = new ArrayList<>();

        // Priorité 1 : des rôles réellement présents dans la partie, de camp différent
        List<String> inPlayOtherCamp = new ArrayList<>(inPlay);
        inPlayOtherCamp.removeIf(name -> roleTeams.get(name) == targetTeam);
        Collections.shuffle(inPlayOtherCamp);
        addUpTo(decoys, inPlayOtherCamp, 2);

        // Priorité 2 : n'importe quel rôle existant de camp différent
        if (decoys.size() < 2) {
            List<String> otherCamp = new ArrayList<>(roleTeams.keySet());
            otherCamp.removeAll(decoys);
            otherCamp.remove(targetName);
            otherCamp.removeIf(name -> roleTeams.get(name) == targetTeam);
            Collections.shuffle(otherCamp);
            addUpTo(decoys, otherCamp, 2);
        }

        // Dernier recours : n'importe quel autre rôle existant
        if (decoys.size() < 2) {
            List<String> anyOther = new ArrayList<>(roleTeams.keySet());
            anyOther.removeAll(decoys);
            anyOther.remove(targetName);
            Collections.shuffle(anyOther);
            addUpTo(decoys, anyOther, 2);
        }

        return decoys;

    }

    private void addUpTo(List<String> target, List<String> pool, int max) {

        for (String value : pool) {

            if (target.size() >= max) {
                return;
            }

            target.add(value);

        }

    }

}
