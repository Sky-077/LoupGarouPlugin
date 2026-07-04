package fr.dmall.loupgarou.role.solo;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChasseurDePrimesRole extends Role {

    public static final int CONTRACT_COUNT = 2;

    private final List<UUID> contracts = new ArrayList<>();
    private final List<UUID> fulfilled = new ArrayList<>();

    public ChasseurDePrimesRole() {
        super("Chasseur de Primes", RoleTeam.NEUTRAL);
    }

    public void setContracts(List<UUID> contracts) {
        this.contracts.clear();
        this.contracts.addAll(contracts);
    }

    public boolean isContract(UUID uuid) {
        return contracts.contains(uuid);
    }

    public boolean isFulfilled(UUID uuid) {
        return fulfilled.contains(uuid);
    }

    public void fulfillContract(UUID uuid) {
        fulfilled.add(uuid);
    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Vous êtes solitaire : vous gagnez seul en étant le dernier camp survivant.");
        lines.add("Vous avez des contrats secrets. Éliminer une cible remplit son contrat et vous rapporte du matériel utile :");

        for (UUID target : contracts) {

            String name = Bukkit.getOfflinePlayer(target).getName();

            if (name == null) {
                name = "Inconnu";
            }

            lines.add(" - " + name + (isFulfilled(target) ? " §a(rempli)" : ""));

        }

        return lines.toArray(new String[0]);

    }

}
