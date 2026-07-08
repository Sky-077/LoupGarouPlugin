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
                "Comme tout rôle solitaire, votre victoire ne peut être que personnelle.",
                "Force I reste active en continu jusqu'à la moitié de l'épisode 6.",
                "Éliminer votre toute première victime vous fait hériter intégralement de son rôle : ses pouvoirs, ses commandes et ses effets deviennent les vôtres.",
                "Votre condition de victoire ne change pas pour autant : rester l'unique survivant demeure obligatoire.",
                "L'instant où vous imitez un rôle, votre Force I disparaît aussitôt.",
        };
    }

}
