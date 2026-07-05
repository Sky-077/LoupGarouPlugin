package fr.dmall.loupgarou.role;

import fr.dmall.loupgarou.manager.Manager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.role.loup.LoupGarouRole;
import fr.dmall.loupgarou.role.loup.PereDesLoupsRole;
import fr.dmall.loupgarou.role.loup.WolfRole;
import fr.dmall.loupgarou.role.solo.AngeRole;
import fr.dmall.loupgarou.role.solo.ChasseurDePrimesRole;
import fr.dmall.loupgarou.role.solo.LoupBlancRole;
import fr.dmall.loupgarou.role.village.ChasseurRole;
import fr.dmall.loupgarou.role.village.CupidonRole;
import fr.dmall.loupgarou.role.village.IdiotDuVillageRole;
import fr.dmall.loupgarou.role.village.PetiteFilleRole;
import fr.dmall.loupgarou.role.village.SalvateurRole;
import fr.dmall.loupgarou.role.village.SorciereRole;
import fr.dmall.loupgarou.role.village.VillageoisRole;
import fr.dmall.loupgarou.role.village.VoyanteRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoleManager implements Manager {

    private final Map<String, Integer> gameRoles = new LinkedHashMap<>();

    @Override
    public void enable() {

        RoleFactory.register("villageois", VillageoisRole::new);
        RoleFactory.register("loup-garou", LoupGarouRole::new);
        RoleFactory.register("pere-des-loups", PereDesLoupsRole::new);
        RoleFactory.register("petite-fille", PetiteFilleRole::new);
        RoleFactory.register("voyante", VoyanteRole::new);
        RoleFactory.register("sorciere", SorciereRole::new);
        RoleFactory.register("chasseur", ChasseurRole::new);
        RoleFactory.register("cupidon", CupidonRole::new);
        RoleFactory.register("chasseur-de-primes", ChasseurDePrimesRole::new);
        RoleFactory.register("loup-blanc", LoupBlancRole::new);
        RoleFactory.register("ange", AngeRole::new);
        RoleFactory.register("salvateur", SalvateurRole::new);
        RoleFactory.register("idiot-du-village", IdiotDuVillageRole::new);

    }

    @Override
    public void disable() {

        gameRoles.clear();

    }

    public void addGameRole(String name, int amount) {

        if (!RoleFactory.exists(name)) {
            throw new IllegalArgumentException("Rôle inconnu : " + name);
        }

        gameRoles.merge(name.toLowerCase(), amount, Integer::sum);

    }

    public boolean removeGameRole(String name) {
        return gameRoles.remove(name.toLowerCase()) != null;
    }

    public void clearGameRoles() {
        gameRoles.clear();
    }

    public Map<String, Integer> getGameRoles() {
        return Collections.unmodifiableMap(gameRoles);
    }

    public void assignRoles(List<LGPlayer> players) {

        List<String> pool = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : gameRoles.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                pool.add(entry.getKey());
            }
        }

        Collections.shuffle(pool);

        for (int i = 0; i < players.size(); i++) {

            Role role = (i < pool.size())
                    ? RoleFactory.create(pool.get(i))
                    : new VillageoisRole();

            players.get(i).setRole(role);

        }

        assignKnownWolves(players);

    }

    private void assignKnownWolves(List<LGPlayer> players) {

        List<LGPlayer> wolves = players.stream()
                .filter(lgPlayer -> lgPlayer.getRole() != null && lgPlayer.getRole().getTeam() == RoleTeam.LOUP)
                .collect(Collectors.toList());

        List<UUID> wolfUuids = wolves.stream()
                .map(LGPlayer::getUuid)
                .collect(Collectors.toList());

        for (LGPlayer wolf : wolves) {

            List<UUID> otherWolves = wolfUuids.stream()
                    .filter(uuid -> !uuid.equals(wolf.getUuid()))
                    .collect(Collectors.toList());

            ((WolfRole) wolf.getRole()).setKnownWolves(otherWolves);

        }

        for (LGPlayer lgPlayer : players) {

            if (lgPlayer.getRole() instanceof LoupBlancRole) {
                ((LoupBlancRole) lgPlayer.getRole()).setKnownWolves(wolfUuids);
            }

        }

    }

}