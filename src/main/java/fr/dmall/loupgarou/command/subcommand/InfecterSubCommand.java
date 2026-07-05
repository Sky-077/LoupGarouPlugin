package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.CorruptionManager;
import fr.dmall.loupgarou.game.DeathManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.game.GameState;
import fr.dmall.loupgarou.game.VictoryChecker;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.loup.LoupGarouRole;
import fr.dmall.loupgarou.role.loup.PereDesLoupsRole;
import fr.dmall.loupgarou.role.loup.WolfRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InfecterSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "infecter";
    }

    @Override
    public String getDescription() {
        return "Transforme un joueur corrompu à 100% en Loup-Garou au lieu de le laisser mourir (Père des Loups).";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player infector = (Player) sender;

        PlayerManager playerManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(PlayerManager.class);

        LGPlayer lgInfector = playerManager.get(infector);

        if (lgInfector == null || !lgInfector.isAlive()) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande.");
            return true;
        }

        Role role = lgInfector.getRole();

        if (!(role instanceof PereDesLoupsRole)) {
            sender.sendMessage("§cVous n'êtes pas le Père des Loups.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg infecter <joueur>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        DeathManager deathManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(DeathManager.class);

        if (!deathManager.hasConversionOffer(target)) {
            sender.sendMessage("§cCe joueur n'attend pas de décision d'infection.");
            return true;
        }

        LGPlayer lgTarget = playerManager.get(target);

        if (lgTarget == null) {
            sender.sendMessage("§cCe joueur ne fait pas partie de la partie en cours.");
            return true;
        }

        GameManager gameManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(GameManager.class);

        Game game = gameManager.getCurrentGame();

        deathManager.consumeConversionOffer(target);
        deathManager.revive(target);

        List<LGPlayer> existingWolves = game.getPlayers().stream()
                .filter(p -> p.getRole() instanceof WolfRole)
                .collect(Collectors.toList());

        List<UUID> existingWolfUuids = existingWolves.stream()
                .map(LGPlayer::getUuid)
                .collect(Collectors.toList());

        LoupGarouRole newRole = new LoupGarouRole();
        newRole.setKnownWolves(existingWolfUuids);
        lgTarget.setRole(newRole);

        for (LGPlayer wolf : existingWolves) {
            ((WolfRole) wolf.getRole()).addKnownWolf(target.getUniqueId());
        }

        CorruptionManager corruptionManager = LoupGarouPlugin.getInstance()
                .getManagerRegistry()
                .getManager(CorruptionManager.class);

        corruptionManager.reset(target.getUniqueId());

        if (game.getState() == GameState.NIGHT) {
            newRole.onNight(target);
        }

        target.sendMessage("§4Le Père des Loups vous a infecté... Vous êtes désormais Loup-Garou !");
        infector.sendMessage("§4Vous avez infecté " + target.getName() + " ! Il rejoint la meute.");

        VictoryChecker.check();

        return true;
    }

}
