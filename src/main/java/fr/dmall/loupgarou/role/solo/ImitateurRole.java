package fr.dmall.loupgarou.role.solo;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ImitateurRole extends Role {

    private static final int STRENGTH_CUTOFF_EPISODE = 6;

    private boolean strengthActive = true;

    public ImitateurRole() {
        super("Imitateur", RoleTeam.NEUTRAL);
    }

    @Override
    public void onDay(Player player) {
        updateStrength(player);
    }

    @Override
    public void onNight(Player player) {
        updateStrength(player);
    }

    private void updateStrength(Player player) {

        if (!strengthActive) {
            return;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        boolean pastMidpoint = game.getEpisode() > STRENGTH_CUTOFF_EPISODE
                || (game.getEpisode() == STRENGTH_CUTOFF_EPISODE && game.getState() == GameState.NIGHT);

        if (pastMidpoint) {
            strengthActive = false;
            player.removePotionEffect(PotionEffectType.STRENGTH);
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 0, false, false));

    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Vous devez gagner seul.",
                "Vous disposez de Force I en permanence jusqu'au milieu de l'épisode 6.",
                "Le premier joueur que vous tuez vous transmet son rôle : vous volez ses pouvoirs, ses commandes et ses effets.",
                "Vous conservez cependant votre condition de victoire : vous devez gagner seul dans tous les cas.",
                "Imiter un rôle vous fait immédiatement perdre votre Force I.",
        };
    }

}
