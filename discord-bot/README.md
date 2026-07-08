# Bot Discord — Le Cercle des Loups

Bot Discord séparé du plugin Minecraft (Node.js). Il ajoute une commande slash par rôle (`/villageois`, `/loup_garou`, `/sorciere`, ...) : chacune répond en privé (ephemeral, visible seulement par celui qui l'utilise) avec le camp et les pouvoirs du rôle.

## Installation

1. Créer une application sur https://discord.com/developers/applications, onglet **Bot** → récupérer le token, onglet **General Information** → récupérer l'Application ID (= `CLIENT_ID`).
2. Inviter le bot sur le serveur via OAuth2 → URL Generator, scope `bot` + `applications.commands`.
3. Copier `.env.example` en `.env` et remplir :
   - `DISCORD_TOKEN` : le token du bot
   - `CLIENT_ID` : l'Application ID
   - `GUILD_ID` : l'ID du serveur "Le Cercle des Loups" (clic droit sur le serveur → Copier l'identifiant, mode développeur à activer dans les paramètres Discord). Recommandé : les commandes de guilde s'activent instantanément, contrairement aux commandes globales (~1h de propagation).
4. `npm install`
5. `npm run deploy-commands` (à refaire seulement si la liste des rôles change)
6. `npm start` pour lancer le bot

## Mettre à jour les textes de rôle

Les textes sont dans `src/roles.js`, un objet par rôle. Ils sont recopiés depuis les `getInstructions()` du plugin Java — si ces textes changent côté plugin, il faut répercuter manuellement le changement ici (pas de lien automatique entre les deux projets).
