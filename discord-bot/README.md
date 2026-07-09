# Bot Discord — Le Cercle des Loups

Bot Discord séparé du plugin Minecraft (Node.js). Il ajoute :
- Une commande slash par rôle (`/villageois`, `/loup_garou`, `/sorciere`, ...) : chacune répond en privé (ephemeral, visible seulement par celui qui l'utilise) avec le camp et les pouvoirs du rôle.
- Un système de tickets (`src/tickets.js`) : `/probleme`, `/bug`, `/suggestion` et `/candidature` ouvrent un formulaire privé, puis créent un **fil de discussion privé** (visible uniquement par l'auteur et le staff) où la conversation continue, avec des boutons d'action réservés au staff (Pris en charge / Résolu / Ignoré). Une notification est aussi postée dans le salon de logs staff pour signaler le nouveau ticket.

## Installation

1. Créer une application sur https://discord.com/developers/applications, onglet **Bot** → récupérer le token, onglet **General Information** → récupérer l'Application ID (= `CLIENT_ID`).
2. Inviter le bot sur le serveur via OAuth2 → URL Generator, scope `bot` + `applications.commands`.
3. Copier `.env.example` en `.env` et remplir :
   - `DISCORD_TOKEN` : le token du bot
   - `CLIENT_ID` : l'Application ID
   - `GUILD_ID` : l'ID du serveur "Le Cercle des Loups" (clic droit sur le serveur → Copier l'identifiant, mode développeur à activer dans les paramètres Discord). Recommandé : les commandes de guilde s'activent instantanément, contrairement aux commandes globales (~1h de propagation).
   - `ROLES_CHANNEL_ID` (optionnel) : l'ID du salon `#rôles` — si renseigné, les 22 commandes `/<role>` ne sont utilisables que dans ce salon (refusé ailleurs). Laisser vide = pas de restriction.
4. Pour le système de tickets, compléter aussi dans `.env` :
   - `LOG_CHANNEL_ID` : l'ID du salon **parent** des fils de tickets (ex: `#tickets`) — doit être visible par tout le monde (sinon l'ajout de l'auteur au fil échoue avec une erreur "Missing Access"), mais avec "Envoyer des messages" refusé pour `@everyone` pour empêcher toute discussion directe dedans (laisser "Envoyer des messages dans les fils" autorisé).
   - `STAFF_NOTIFY_CHANNEL_ID` : l'ID du salon staff (ex: `#logs-modération`, visible seulement par le staff) où une notification est postée à chaque nouveau ticket, avec un lien vers le fil.
   - `STAFF_ROLE_ID` : l'ID du rôle autorisé à cliquer sur les boutons d'action (ex: le rôle Modérateur)
   - `CHANNEL_PROBLEME_ID`, `CHANNEL_BUG_ID`, `CHANNEL_SUGGESTION_ID`, `CHANNEL_CANDIDATURE_ID` (optionnel) : si renseignés, chaque commande n'est utilisable que dans le salon correspondant (ex: `/bug` refusé ailleurs que `#bugs`), et **le bot supprime automatiquement** tout message qui n'est pas une commande dans ces salons (`handleChannelMessage`). Laisser vide = pas de restriction/suppression.

   Important : laisser "Envoyer des messages" **autorisé** pour `@everyone` dans ces salons — le refuser désactive aussi la zone de saisie des commandes slash côté client Discord (les deux sont liées dans l'interface, même si ce sont des permissions distinctes côté API). C'est le bot qui fait le ménage, pas une permission de salon.

   Permissions Discord à vérifier :
   - Le **bot** a besoin, dans le salon `LOG_CHANNEL_ID`, de "Créer des fils de discussion privés" et "Envoyer des messages dans les fils".
   - Le **rôle staff** a besoin de la permission "Gérer les fils de discussion" pour voir les fils privés sans y avoir été ajouté explicitement.
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
