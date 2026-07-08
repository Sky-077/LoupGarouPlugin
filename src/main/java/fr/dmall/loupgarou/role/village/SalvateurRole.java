package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class SalvateurRole extends Role {

    private boolean protectAvailable = true;
    private UUID protectedUuid;

    public SalvateurRole() {
        super("Salvateur", RoleTeam.VILLAGE);
    }

    @Override
    public void onDay(Player player) {

        if (protectedUuid != null) {

            Player protege = Bukkit.getPlayer(protectedUuid);

            if (protege != null) {
                protege.removePotionEffect(PotionEffectType.RESISTANCE);
            }

            protectedUuid = null;

        }

        protectAvailable = true;

    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Le Village est votre camp, et sa victoire scelle la vôtre.",
                "/lg proteger <joueur> place un joueur sous protection jusqu'à la fin de l'épisode en cours, une fois par épisode.",
                "La cible bénéficie de Résistance I et d'une réduction de 50% des dégâts de chute, sans jamais en être avertie.",
        };
    }

    public boolean isProtectAvailable() {
        return protectAvailable;
    }

    public void consumeProtect() {
        protectAvailable = false;
    }

    public void setProtectedUuid(UUID uuid) {
        this.protectedUuid = uuid;
    }

    public UUID getProtectedUuid() {
        return protectedUuid;
    }

    public boolean isProtecting(UUID uuid) {
        return uuid != null && uuid.equals(protectedUuid);
    }

}
