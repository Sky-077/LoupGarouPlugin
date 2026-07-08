package fr.dmall.loupgarou.role.solo;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChasseurDePrimesRole extends Role {

    public static final int CONTRACT_COUNT = 2;

    private final List<UUID> allContracts = new ArrayList<>();
    private final List<UUID> fulfilled = new ArrayList<>();
    private UUID currentContract;
    private Integer nextContractEpisode;

    public ChasseurDePrimesRole() {
        super("Chasseur de Primes", RoleTeam.NEUTRAL);
    }

    public boolean hasContractOn(UUID uuid) {
        return allContracts.contains(uuid);
    }

    public int getContractsIssued() {
        return allContracts.size();
    }

    public void issueContract(UUID target) {
        allContracts.add(target);
        currentContract = target;
        nextContractEpisode = null;
    }

    public boolean isContract(UUID uuid) {
        return uuid.equals(currentContract);
    }

    public boolean isFulfilled(UUID uuid) {
        return fulfilled.contains(uuid);
    }

    public void fulfillContract(UUID uuid, int episode) {

        fulfilled.add(uuid);
        currentContract = null;

        if (allContracts.size() < CONTRACT_COUNT) {
            nextContractEpisode = episode + 1;
        }

    }

    public void cancelContract(UUID uuid, int episode) {

        if (!uuid.equals(currentContract)) {
            return;
        }

        currentContract = null;

        if (allContracts.size() < CONTRACT_COUNT) {
            nextContractEpisode = episode + 1;
        }

    }

    public boolean isSecondContractDue(int episode) {
        return nextContractEpisode != null && episode >= nextContractEpisode;
    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Rôle solitaire : la victoire exige d'être le dernier en vie, tous les autres camps et solitaires éliminés.");
        lines.add("Un premier contrat secret vous est confié dès l'activation du PVP ; le suivant tombe à l'épisode qui suit la réussite du précédent.");
        lines.add("Abattre la cible actuellement visée valide le contrat et vous octroie du matériel.");

        if (currentContract != null) {

            String name = Bukkit.getOfflinePlayer(currentContract).getName();

            if (name == null) {
                name = "Inconnu";
            }

            lines.add("§eCible actuelle : " + name);

        } else if (allContracts.size() < CONTRACT_COUNT) {
            lines.add("§fAucune cible ne vous est actuellement assignée.");
        } else {
            lines.add("§fTous vos contrats ont déjà été traités.");
        }

        return lines.toArray(new String[0]);

    }

}
