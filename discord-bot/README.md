# Bot Discord — Le Cercle des Loups

Bot Discord séparé du plugin Minecraft (Node.js). Il ajoute :
- Une commande slash par rôle (`/villageois`, `/loup_garou`, `/sorciere`, ...) : chacune répond en privé (ephemeral, visible seulement par celui qui l'utilise) avec le camp et les pouvoirs du rôle.
- Un système de tickets (`src/tickets.js`) : `/probleme`, `/bug`, `/suggestion` et `/candidature` ouvrent un formulaire privé (rien n'est jamais posté publiquement), et le contenu est relayé dans un salon de logs avec des boutons d'action réservés au staff (Pris en charge / Résolu / Ignoré).

## Installation

1. Créer une application sur https://discord.com/developers/applications, onglet **Bot** → récupérer le token, onglet **General Information** → récupérer l'Application ID (= `CLIENT_ID`).
2. Inviter le bot sur le serveur via OAuth2 → URL Generator, scope `bot` + `applications.commands`.
3. Copier `.env.example` en `.env` et remplir :
   - `DISCORD_TOKEN` : le token du bot
   - `CLIENT_ID` : l'Application ID
   - `GUILD_ID` : l'ID du serveur "Le Cercle des Loups" (clic droit sur le serveur → Copier l'identifiant, mode développeur à activer dans les paramètres Discord). Recommandé : les commandes de guilde s'activent instantanément, contrairement aux commandes globales (~1h de propagation).
4. Pour le système de tickets, compléter aussi dans `.env` :
   - `LOG_CHANNEL_ID` : l'ID du salon où les tickets sont relayés (ex: `#logs-modération`)
   - `STAFF_ROLE_ID` : l'ID du rôle autorisé à cliquer sur les boutons d'action (ex: le rôle Modérateur)
5. `npm install`
6. `npm run deploy-commands` (à refaire seulement si la liste des rôles change)
7. `npm start` pour lancer le bot

## Mettre à jour les textes de rôle

Les textes sont dans `src/roles.js`, un objet par rôle. Ils sont recopiés depuis les `getInstructions()` du plugin Java — si ces textes changent côté plugin, il faut répercuter manuellement le changement ici (pas de lien automatique entre les deux projets).

## Hébergement permanent (Replit + UptimeRobot)

Pour que le bot reste en ligne 24/7 sans payer ni fournir de carte bancaire :

1. Créer un compte sur https://replit.com (gratuit, sans CB) et importer ce dossier `discord-bot` (import depuis GitHub, ou créer un Repl Node.js et coller les fichiers).
2. Dans l'onglet **Secrets** du Repl (pas de fichier `.env`, Replit gère ça séparément et de façon chiffrée), ajouter `DISCORD_TOKEN`, `CLIENT_ID`, `GUILD_ID`.
3. Lancer le Repl une première fois (`npm install` puis `npm run deploy-commands` dans la console Replit, une seule fois), puis `npm start` (ou configurer la commande `run` du Repl sur `npm start`).
4. Le Repl expose une URL web (visible dans l'onglet Webview) — c'est le petit serveur de `src/keepalive.js` qui répond "Le Cercle des Loups — bot en ligne".
5. Sur https://uptimerobot.com (gratuit, sans CB), créer un moniteur HTTP(s) pointant vers cette URL, intervalle 5 minutes — ça empêche le Repl de s'endormir par inactivité.

Limite connue : ce n'est pas un hébergement garanti par Replit pour ce cas d'usage (contournement de la mise en veille), donc de rares coupures/redémarrages peuvent survenir. Suffisant pour un petit bot comme celui-ci.
