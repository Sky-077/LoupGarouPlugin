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
        lines.add("Vous êtes solitaire : vous gagnez seul en étant l'unique survivant, tout le monde éliminé (y compris les autres solitaires).");
        lines.add("À l'activation du PVP, vous recevez un premier contrat secret. Le second arrive à l'épisode suivant votre premier succès.");
        lines.add("Éliminer votre cible en cours remplit le contrat et vous rapporte du matériel utile.");

        if (currentContract != null) {

            String name = Bukkit.getOfflinePlayer(currentContract).getName();

            if (name == null) {
                name = "Inconnu";
            }

            lines.add("§eContrat en cours : " + name);

        } else if (allContracts.size() < CONTRACT_COUNT) {
            lines.add("§fAucun contrat actif pour l'instant.");
        } else {
            lines.add("§fVous n'avez plus de contrat à accomplir.");
        }

        return lines.toArray(new String[0]);

    }

}
