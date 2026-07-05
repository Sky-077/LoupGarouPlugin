package fr.dmall.loupgarou.command.subcommand;

import fr.dmall.loupgarou.LoupGarouPlugin;
import fr.dmall.loupgarou.game.AngeManager;
import fr.dmall.loupgarou.game.Game;
import fr.dmall.loupgarou.game.GameManager;
import fr.dmall.loupgarou.player.LGPlayer;
import fr.dmall.loupgarou.player.PlayerManager;
import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import fr.dmall.loupgarou.role.solo.AngeRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AngeSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "ange";
    }

    @Override
    public String getDescription() {
        return "Choisit votre forme d'Ange : dechu ou gardien (une fois par partie).";
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

        if (!(role instanceof AngeRole)) {
            sender.sendMessage("§cVous n'êtes pas l'Ange.");
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

        AngeRole ange = (AngeRole) role;

        if (ange.getForm() != AngeRole.Form.UNDECIDED) {
            sender.sendMessage("§cVous avez déjà choisi votre forme.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage : /lg ange <dechu|gardien>");
            return true;
        }

        AngeRole.Form form;

        if (args[1].equalsIgnoreCase("dechu")) {
            form = AngeRole.Form.DECHU;
        } else if (args[1].equalsIgnoreCase("gardien")) {
            form = AngeRole.Form.GARDIEN;
        } else {
            sender.sendMessage("§cUsage : /lg ange <dechu|gardien>");
            return true;
        }

        List<LGPlayer> candidates = new ArrayList<>();

        for (LGPlayer other : game.getPlayers()) {

            if (!other.getUuid().equals(lgPlayer.getUuid()) && other.isAlive()) {
                candidates.add(other);
            }

        }

        if (candidates.isEmpty()) {
            sender.sendMessage("§cAucun joueur disponible pour l'instant.");
            return true;
        }

        Collections.shuffle(candidates);
        LGPlayer linked = candidates.get(0);

        ange.chooseForm(form);
        ange.setLinkedPlayer(linked.getUuid());

        double hearts = (form == AngeRole.Form.DECHU) ? 12 : 15;
        AngeManager.applyHearts(player, hearts);

        Player linkedPlayerEntity = Bukkit.getPlayer(linked.getUuid());
        String linkedName = (linkedPlayerEntity != null) ? linkedPlayerEntity.getName() : "Inconnu";
        String linkedRoleName = (linked.getRole() != null) ? linked.getRole().getName() : "Inconnu";

        if (form == AngeRole.Form.DECHU) {

            player.sendMessage("§cVous êtes désormais l'Ange Déchu ! Votre cible : "
                    + linkedName + " (" + linkedRoleName + "). 12 cœurs.");

        } else {

            player.sendMessage("§bVous êtes désormais l'Ange Gardien ! Votre protégé : "
                    + linkedName + " (" + linkedRoleName + "). 15 cœurs.");

            RoleTeam protegeTeam = linked.getEffectiveTeam();

            if (protegeTeam != null) {
                lgPlayer.setTeamOverride(protegeTeam);
            }

        }

        return true;
    }

}
