package fr.dmall.loupgarou.command.subcommand;

import org.bukkit.command.CommandSender;

public class HelpSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Affiche la liste des commandes.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        sender.sendMessage("§6========== LoupGarou ==========");
        sender.sendMessage("§e/lg help §7- Affiche cette aide");
        sender.sendMessage("§e/lg info §7- Informations de la partie");
        sender.sendMessage("§e/lg start §7- Lance la partie avec les joueurs inscrits (/lg join) [réservé à l'hôte]");
        sender.sendMessage("§e/lg stop §7- Arrête la partie [réservé à l'hôte, ou à un OP en secours]");
        sender.sendMessage("§e/lg host <add|remove|list|claim|release> §7- Gère l'hôte de la partie");
        sender.sendMessage("§e/lg delais <minjoueurs|invincibilite|revelation|pvp|vote> [valeur] §7- Configure les délais de partie [réservé à l'hôte]");
        sender.sendMessage("§e/lg menu §7- Ouvre le menu de paramètres de la partie [réservé à l'hôte]");
        sender.sendMessage("§e/lg role §7- Configure les rôles (add/remove/list/clear)");
        sender.sendMessage("§e/lg me §7- Affiche votre rôle actuel");
        sender.sendMessage("§e/lg sonder <joueur> §7- Sonde un joueur (Voyante, la nuit)");
        sender.sendMessage("§e/lg infecter <joueur> §7- Accepte de transformer un joueur corrompu à 100% en Loup-Garou (Père des Loups)");
        sender.sendMessage("§e/lg laissermourir <joueur> §7- Refuse et laisse mourir un joueur corrompu à 100% (Père des Loups)");
        sender.sendMessage("§e/lg regle §7- Réaffiche l'explication de votre rôle");
        sender.sendMessage("§e/lg soigner <joueur> §7- Potion de vie (Sorcière)");
        sender.sendMessage("§e/lg empoisonner <joueur> §7- Potion de mort (Sorcière)");
        sender.sendMessage("§e/lg tirer <joueur> §7- Riposte en mourant (Chasseur)");
        sender.sendMessage("§e/lg forcestart §7- [Debug] Force le lancement sans minimum de joueurs");
        sender.sendMessage("§e/lg bordure <taille> §7- Configure la bordure de la prochaine partie");
        sender.sendMessage("§e/lg forcepvp §7- [Debug] Active immédiatement le PVP");
        sender.sendMessage("§e/lg join §7- S'inscrit pour la prochaine partie");
        sender.sendMessage("§e/lg leave §7- Annule son inscription");
        sender.sendMessage("§e/lg forcereveal §7- [Debug] Force la révélation immédiate des rôles");
        sender.sendMessage("§e/lg lier <joueur1> <joueur2> §7- Lie deux joueurs par l'amour (Cupidon)");
        sender.sendMessage("§e/lg lobbyspawn §7- [OP] Définit le spawn du lobby à votre position actuelle");
        sender.sendMessage("§e/lg forcevote §7- [Debug] Ouvre immédiatement le vote");
        sender.sendMessage("§e/lg ange <dechu|gardien> §7- Choisit votre forme d'Ange");
        sender.sendMessage("§e/lg regen §7- Régénération pour votre protégé sous 4 cœurs (Ange Gardien)");
        sender.sendMessage("§e/lg proteger <joueur> §7- Protège un joueur jusqu'à la fin de l'épisode (Salvateur)");
        sender.sendMessage("§e/lg loups <message> §7- Chat privé de la meute la nuit (Loup-Garou, Père des Loups, Loup Blanc)");
        sender.sendMessage("§e/lg conferer <joueur> §7- Offre 1 cœur permanent, délivré 3 min plus tard (Bienfaiteur)");
        sender.sendMessage("§e/lg folie §7- Active la Folie Incendiaire, 1 minute (Feu Follet)");

        return true;
    }
}