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
                "Vous devez gagner avec le Village.",
                "Une fois par épisode, protégez un joueur jusqu'à la fin de l'épisode avec /lg proteger <joueur>.",
                "Le joueur protégé reçoit Résistance I et 50% de réduction des dégâts de chute, sans en être informé.",
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
