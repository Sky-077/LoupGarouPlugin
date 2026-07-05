package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.solo.FeuFolletRole;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FolieSubCommand implements SubCommand {

    private static final int SPEED_DURATION_TICKS = 20 * 60; // 1 minute

    @Override
    public String getName() {
        return "folie";
    }

    @Override
    public String getDescription() {
        return "Active la Folie Incendiaire : Speed I pendant 1 minute, met le feu au corps-à-corps (Feu Follet, 1 fois toutes les 10 minutes).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgPlayer = playerManager.get(player);

        if (lgPlayer == null || !lgPlayer.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgPlayer.getRole();

        if (!(role instanceof FeuFolletRole)) {
            sender.sendMessage("§cVous n'êtes pas le Feu Follet.");
            return true;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        if (!game.isRevealed()) {
            sender.sendMessage("§cLes rôles n'ont pas encore été révélés.");
            return true;
        }

        FeuFolletRole feuFolletRole = (FeuFolletRole) role;

        if (!feuFolletRole.isFolieAvailable()) {
            sender.sendMessage("§cVotre Folie Incendiaire n'est pas encore rechargée (" + feuFolletRole.getFolieRemainingSeconds() + "s restantes).");
            return true;
        }

        feuFolletRole.activateFolie();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SPEED_DURATION_TICKS, 0, false, true));
        player.sendMessage("§6Votre Folie Incendiaire s'éveille ! Vos coups au corps-à-corps mettent le feu pendant 1 minute.");

        return true;
    }

}
