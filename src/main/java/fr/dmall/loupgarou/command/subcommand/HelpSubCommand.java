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
        sender.sendMessage("§e/lg help §f- Affiche cette aide");
        sender.sendMessage("§e/lg info §f- Informations de la partie");
        sender.sendMessage("§e/lg start §f- Lance la partie avec les joueurs inscrits (/lg join) [réservé à l'hôte]");
        sender.sendMessage("§e/lg stop §f- Arrête la partie [réservé à l'hôte, ou à un OP en secours]");
        sender.sendMessage("§e/lg host <add|remove|list|claim|release> §f- Gère l'hôte de la partie");
        sender.sendMessage("§e/lg delais <minjoueurs|invincibilite|revelation|pvp|vote|rapide> [valeur] §f- Configure les délais de partie [réservé à l'hôte]");
        sender.sendMessage("§e/lg menu §f- Ouvre le menu de paramètres de la partie [réservé à l'hôte]");
        sender.sendMessage("§e/lg role §f- Configure les rôles (add/remove/list/clear)");
        sender.sendMessage("§e/lg me §f- Affiche votre rôle et ses pouvoirs");
        sender.sendMessage("§e/lg sonder <joueur> §f- Sonde un joueur (Voyante, la nuit)");
        sender.sendMessage("§e/lg infecter <joueur> §f- Accepte de transformer un joueur corrompu à 100% en Loup-Garou (Père des Loups)");
        sender.sendMessage("§e/lg laissermourir <joueur> §f- Refuse et laisse mourir un joueur corrompu à 100% (Père des Loups)");
        sender.sendMessage("§e/lg regle §f- Explique les règles générales du jeu");
        sender.sendMessage("§e/lg soigner <joueur> §f- Potion de vie (Sorcière)");
        sender.sendMessage("§e/lg empoisonner <joueur> §f- Potion de mort (Sorcière)");
        sender.sendMessage("§e/lg tirer <joueur> §f- Riposte en mourant (Chasseur)");
        sender.sendMessage("§e/lg forcestart §f- [Debug] Force le lancement sans minimum de joueurs");
        sender.sendMessage("§e/lg bordure <taille> §f- Configure la bordure de la prochaine partie");
        sender.sendMessage("§e/lg forcepvp §f- [Debug] Active immédiatement le PVP");
        sender.sendMessage("§e/lg join §f- S'inscrit pour la prochaine partie");
        sender.sendMessage("§e/lg leave §f- Annule son inscription");
        sender.sendMessage("§e/lg forcereveal §f- [Debug] Force la révélation immédiate des rôles");
        sender.sendMessage("§e/lg lier <joueur1> <joueur2> §f- Lie deux joueurs par l'amour (Cupidon)");
        sender.sendMessage("§e/lg lobbyspawn §f- [OP] Définit le spawn du lobby à votre position actuelle");
        sender.sendMessage("§e/lg forcevote §f- [Debug] Ouvre immédiatement le vote");
        sender.sendMessage("§e/lg ange <dechu|gardien> §f- Choisit votre forme d'Ange");
        sender.sendMessage("§e/lg regen §f- Régénération pour votre protégé sous 4 cœurs (Ange Gardien)");
        sender.sendMessage("§e/lg proteger <joueur> §f- Protège un joueur jusqu'à la fin de l'épisode (Salvateur)");
        sender.sendMessage("§e/lg loups <message> §f- Chat privé de la meute la nuit (Loup-Garou, Père des Loups, Loup Blanc)");
        sender.sendMessage("§e/lg conferer <joueur> §f- Offre 1 cœur permanent, délivré 3 min plus tard (Bienfaiteur)");
        sender.sendMessage("§e/lg folie §f- Active la Folie Incendiaire, 1 minute (Feu Follet)");

        return true;
    }
}