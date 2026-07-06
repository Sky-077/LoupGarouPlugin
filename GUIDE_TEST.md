# Guide de test — LoupGarouPlugin

## Toutes les commandes

### Commandes générales

| Commande | Description |
|---|---|
| `/lg help` | Liste des commandes |
| `/lg info` | État de la partie + liste des rôles des joueurs |
| `/lg join` | S'inscrit pour la prochaine partie |
| `/lg leave` | Annule son inscription |
| `/lg start` | Lance la partie avec les joueurs inscrits (min. 3, sauf via `/lg forcestart`) — **réservé à l'Hôte actif** |
| `/lg stop` | Arrête la partie, réinitialise les inscriptions — **réservé à l'Hôte actif, ou à un OP en secours** |
| `/lg host add <joueur>` | Rend un joueur éligible à devenir Hôte (OP uniquement) |
| `/lg host remove <joueur>` | Retire l'éligibilité d'un joueur (OP uniquement, refusé pour `Skytag07` qui est permanent) |
| `/lg host list` | Liste les joueurs éligibles et affiche l'Hôte actif s'il y en a un |
| `/lg host claim` | Revendique le rôle d'Hôte (si éligible et aucun Hôte actif) |
| `/lg host release` | Quitte le rôle d'Hôte (si on l'occupe actuellement) |
| `/lg delais [minjoueurs\|invincibilite\|revelation\|pvp\|vote] [valeur]` | Affiche (ouvert à tous) ou modifie (**réservé à l'Hôte actif**) le minimum de joueurs et les 4 délais de partie, en minutes |
| `/lg menu` | Ouvre le menu GUI de paramètres (bordure/min. joueurs/délais/rôles) — **réservé à l'Hôte actif** |
| `/lg me` | Affiche son propre rôle (bloqué tant que les rôles ne sont pas révélés) |
| `/lg regle` | Réaffiche l'explication de son rôle (bloqué tant que non révélé) |
| `/lg bordure <taille>` | Configure la taille de la bordure pour la prochaine partie — **réservé à l'Hôte actif** (sans argument : affiche la valeur actuelle, ouvert à tous) |
| `/lg color <joueur1> <joueur2> ...>` | Ouvre un menu (16 laines colorées) pour colorer le pseudo (au-dessus de la tête + liste Tab) des joueurs listés, visible uniquement par soi-même |

### Configuration des rôles avant une partie

| Commande | Description |
|---|---|
| `/lg role add <role> <nombre>` | Ajoute des rôles au pool (`villageois`, `loup-garou`, `pere-des-loups`, `grand-mechant-loup`, `loup-garou-craintif`, `loup-garou-perfide`, `vilain-petit-loup`, `petite-fille`, `voyante`, `sorciere`, `chasseur`, `cupidon`, `chasseur-de-primes`, `loup-blanc`, `ange`, `salvateur`, `idiot-du-village`, `ancien`, `bienfaiteur`, `feu-follet`, `imitateur`, `joueur-de-flute`) — **réservé à l'Hôte actif** |
| `/lg role remove <role>` | Retire un rôle du pool — **réservé à l'Hôte actif** |
| `/lg role list` | Liste les rôles configurés (ouvert à tous) |
| `/lg role clear` | Réinitialise la configuration — **réservé à l'Hôte actif** |
| `/lg role available` | Liste tous les rôles utilisables avec la bonne orthographe (ouvert à tous) |

### Pouvoirs de rôle (utilisables uniquement une fois les rôles révélés, 10 min après le vrai début)

| Commande | Rôle | Description |
|---|---|---|
| `/lg sonder <joueur>` | Voyante | Découvre le rôle d'un joueur, la nuit, 1x/nuit |
| `/lg infecter <joueur>` | Père des Loups | Accepte de transformer un joueur corrompu à 100% en Loup-Garou (offre cliquable reçue en jeu, 10s, quand un tel joueur meurt de la main d'un loup) |
| `/lg laissermourir <joueur>` | Père des Loups | Refuse l'infection et laisse mourir le joueur corrompu à 100% (même offre) |
| `/lg soigner <joueur>` | Sorcière | Potion de vie : reçue via une offre cliquable de 10s à la mort d'un joueur non infecté, 1x/partie |
| `/lg empoisonner <joueur>` | Sorcière | Potion de mort : retire 2 cœurs de vie maximum, définitivement, à un joueur, 1x/partie |
| `/lg tirer <joueur>` | Chasseur | Riposte pendant sa propre fenêtre de 15s de sursis sur un joueur autre que son tueur, 1x/partie : moitié de vie max en moins (définitif) sur un Loup, quart de vie max en moins (définitif) sur un solitaire, 6 cœurs (non définitif) sur un villageois |
| `/lg lier <joueur1> <joueur2>` | Cupidon | Lie deux joueurs par l'amour, 1x/partie |
| `/lg ange <dechu\|gardien>` | Ange | Choisit sa forme (cible aléatoire assignée), 1x/partie |
| `/lg regen` | Ange Gardien | Donne Régénération I (1 min) à son protégé sous 4 cœurs, 1x/partie |
| `/lg proteger <joueur>` | Salvateur | Protège un joueur (Résistance I + 50% dégâts de chute) jusqu'à la fin de l'épisode, 1x/épisode |
| `/lg loups <message>` | Loup-Garou, Père des Loups, Loup Blanc | Chat privé de la meute, la nuit uniquement — lu aussi par la Petite Fille (lecture seule), tous les pseudos masqués pour tout le monde |
| `/lg conferer <joueur>` | Bienfaiteur | Offre 1 cœur permanent, délivré discrètement 3 minutes plus tard, à 3 joueurs différents max, 1x/5min |
| `/lg folie` | Feu Follet | Active la Folie Incendiaire (Speed I 1 min, met le feu au corps-à-corps), 1x/10min |

### Commandes de debug (réservées aux OP)

| Commande | Description |
|---|---|
| `/lg forcestart` | Force le lancement de la partie sans minimum de joueurs inscrits |
| `/lg forcereveal` | Force la révélation immédiate des rôles sans attendre 10 min |
| `/lg forcepvp` | Active immédiatement le PVP sans attendre 30 min |
| `/lg lobbyspawn` | Définit le spawn du lobby (fin de partie) à la position actuelle de l'OP — persiste dans `config.yml` |
| `/lg forcevote` | Ouvre immédiatement le vote sans attendre le délai de 45 min |
| `/lg honneur <joueur> [valeur]` | Sans valeur : affiche l'honneur du joueur. Avec valeur (-3 à 3) : le règle directement et recalcule l'effet de cœur |

---

## Ce qui n'a jamais été testé en jeu (tout écrit et compilé, mais jamais lancé sur un vrai serveur)

### Cycle de partie de bout en bout
- [🟢] `/lg join` par plusieurs comptes, puis `/lg start` par le host — vérifier que seuls les inscrits reçoivent un rôle et sont téléportés dans le monde de jeu
- [🟢] Les joueurs connectés mais **non inscrits** au moment du `/lg start` : doivent être téléportés dans le monde de partie en mode **spectateur** (pas dans l'ancien monde)
- [🟢] Minimum de 3 joueurs inscrits refusé correctement par `/lg start` (message d'erreur)
- [🟢] `/lg forcestart` (OP) : lance bien la partie avec 1 ou 2 joueurs inscrits seulement
- [🟢] Vérifier que les commandes de debug (`forcestart`, `forcereveal`, `forcepvp`) sont bien refusées à un joueur non-OP

### Système d'hôte (`/lg host`, nécessite plusieurs comptes dont un OP)
- [ ] `Skytag07` est éligible même sans être ajouté via `/lg host add` (permanent en dur dans le code)
- [ ] `/lg host remove Skytag07` refuse toujours ("hôte permanent, impossible de le retirer")
- [ ] Un OP fait `/lg host add <joueur>` : ce joueur reçoit bien le message cliquable "[Oui] devenir l'hôte ?" à sa **prochaine connexion** (pas immédiatement s'il est déjà connecté)
- [ ] Cliquer "[Oui]" (ou taper `/lg host claim`) alors qu'aucun hôte n'est actif : devient hôte, broadcast visible par tous, `/lg info` l'affiche
- [ ] `/lg host claim` par un second joueur éligible alors qu'un hôte est déjà actif : refusé ("X est déjà l'hôte")
- [ ] Un joueur non-éligible qui tape `/lg host claim` : refusé ("vous n'êtes pas éligible")
- [ ] `/lg host release` par l'hôte actif : libère le poste, broadcast visible par tous
- [ ] `/lg host release` par quelqu'un qui n'est pas l'hôte : refusé
- [ ] L'hôte actif qui se **déconnecte** : poste libéré automatiquement (broadcast), sans attendre `/lg host release`
- [ ] Un joueur éligible qui se connecte alors qu'un hôte est déjà actif : ne reçoit **pas** le message de proposition
- [ ] `/lg start` refusé à un joueur qui n'est pas l'hôte actif (même s'il est inscrit via `/lg join`)
- [ ] `/lg stop` refusé à un joueur qui n'est ni l'hôte actif ni OP
- [ ] `/lg stop` fonctionne pour un OP même s'il n'est pas l'hôte (filet de sécurité)
- [ ] `/lg host list` affiche bien tous les éligibles (incluant `Skytag07`) et l'hôte actif s'il y en a un
- [ ] `/lg forcestart`/`/lg forcereveal`/`/lg forcepvp`/`/lg forcevote` (OP) continuent de fonctionner sans avoir besoin d'être l'hôte
- [ ] `/lg bordure <taille>` refusé à un joueur qui n'est pas l'hôte actif ; `/lg bordure` sans argument (lecture) reste accessible à tous
- [ ] `/lg role add/remove/clear` refusés à un joueur qui n'est pas l'hôte actif ; `/lg role list`/`/lg role available` restent accessibles à tous

### Délais de partie configurables (`/lg delais`, nécessite un compte Hôte)
- [ ] `/lg delais` (sans argument) affiche les 5 valeurs actuelles (min joueurs, invincibilité, révélation, pvp, vote), accessible à tous
- [ ] `/lg delais minjoueurs <nombre>` refusé à un non-Hôte ; appliqué par l'Hôte, `/lg start` respecte bien la nouvelle valeur
- [ ] `/lg delais invincibilite <minutes>` : la partie suivante respecte bien la nouvelle durée d'invincibilité (message de lancement affiche la bonne valeur)
- [ ] `/lg delais revelation <minutes>` : la révélation des rôles a bien lieu au bon délai lors de la partie suivante
- [ ] `/lg delais pvp <minutes>` : le PVP s'active bien au bon délai
- [ ] `/lg delais vote <minutes>` : le vote s'ouvre bien au bon délai
- [ ] Tenter de régler la révélation au-dessus du délai PVP (ou le PVP au-dessus du vote) : la valeur est automatiquement plafonnée pour garder l'ordre invincibilité ≤ révélation < pvp < vote, sans message d'erreur bloquant
- [ ] Les valeurs réglées persistent bien pour la partie suivante (comme `/lg bordure`), pas de reset entre deux `/lg start`

### Menu GUI de paramètres (`/lg menu`, nécessite un compte Hôte)
- [ ] `/lg menu` refusé à un joueur qui n'est pas l'hôte actif
- [ ] Menu principal : 4 icônes cliquables (Bordure/Minimum de joueurs/Délais de partie/Pool de rôles), chacune ouvre la bonne sous-page
- [ ] Page Bordure : clic gauche +50, clic droit -50, shift+clic gauche +500, shift+clic droit -500 ; la valeur affichée se met à jour immédiatement après chaque clic ; ne descend jamais sous le minimum (100 blocs)
- [ ] Page Minimum de joueurs : clic gauche +1, clic droit -1, shift = ±5 ; ne descend jamais sous 1
- [ ] Page Délais : les 4 items (invincibilité/révélation/pvp/vote) s'ajustent chacun indépendamment (±1 min, ±10 avec shift) et respectent le même ordre logique que `/lg delais` (pas de valeur incohérente possible)
- [ ] Page Pool de rôles : clic gauche +1/clic droit -1 (±5 avec shift) sur un rôle, le nombre affiché dans le lore se met à jour ; un rôle qui retombe à 0 disparaît bien du pool (`RoleManager.getGameRoles()`)
- [ ] Pagination du pool de rôles : boutons "Page suivante"/"Page précédente" apparaissent seulement quand pertinent (pas de bouton "précédente" sur la première page, pas de "suivante" sur la dernière)
- [ ] Bouton "Retour" sur chaque sous-page ramène bien au menu principal
- [ ] Impossible de déplacer/sortir un item du menu (aucun item ne doit atterrir dans l'inventaire du joueur)
- [ ] Cliquer dans son propre inventaire (en bas de l'écran) pendant que le menu est ouvert ne déclenche aucune modification de paramètre
- [ ] Si l'hôte perd son statut (déconnexion d'un autre hôte, `/lg host release` par un tiers impossible normalement) pendant que le menu est ouvert, le prochain clic le ferme avec un message d'erreur plutôt que d'appliquer un changement
- [ ] Toutes les valeurs modifiées via le menu sont bien reflétées par les commandes texte équivalentes (`/lg bordure`, `/lg delais`, `/lg role list`) et vice-versa

### Génération du monde
- [ ] Le monde de partie est bien vierge à chaque `/lg start`, sans trace de constructions/objets d'une partie précédente (corrigé : chaque partie utilise désormais un nom de monde unique — timestamp en suffixe — au lieu de réutiliser/supprimer le même dossier, qui pouvait rester verrouillé sous Windows et laisser resurgir l'ancien monde) — à revalider
- [🟢] La bordure appliquée correspond bien à la valeur configurée via `/lg bordure`
- [🟢] La recherche de zone évite bien les biomes interdits (océan, île champignon, glace/neige) au centre de la bordure — vérifier visuellement en `/lg forcestart` + explorer
- [🟢] Aucune structure ne génère (village, temple, mineshaft, forteresse...)
- [🟢] Aucun mob hostile/neutre (creeper, zombie, squelette, araignée, enderman...) ne spawn naturellement ; les animaux passifs (vache, cochon, poule) spawnent bien
- [🟢] Impossible de voyager au Nether ou à l'End pendant la partie (message de blocage affiché)
- [🟢] Le temps de génération du monde au lancement (peut geler le serveur une ou deux secondes)

### Limites d'enchantement, pêche et seaux de lave (actif à tout moment, pas seulement en partie)
- [ ] Table d'enchantement sur une armure en fer : jamais plus de Protection III proposé/appliqué
- [ ] Table d'enchantement sur une armure en diamant : jamais plus de Protection II proposé/appliqué
- [ ] Table d'enchantement sur une épée (fer ou diamant) : jamais plus de Tranchant III pour un rôle non-solitaire
- [ ] Table d'enchantement sur une épée pour un rôle solitaire (Loup Blanc, Ange, Feu Follet, Imitateur, Joueur de Flûte, Chasseur de Primes) : peut atteindre Tranchant IV
- [ ] Table d'enchantement sur une hache : suit exactement les mêmes plafonds que l'épée (III normal, IV solitaire)
- [ ] Table d'enchantement sur un arc pour un rôle non-Chasseur/non-solitaire : jamais plus de Puissance III
- [ ] Table d'enchantement sur un arc pour le Chasseur ou un rôle solitaire : peut atteindre Puissance IV
- [ ] Solidité reste obtenable sur n'importe quel objet (armure, épée/hache, arc, outils), sans lien avec les plafonds ci-dessus
- [ ] Table d'enchantement sur un outil (pioche, pelle, houe), un livre vierge, une canne à pêche, un trident... : aucun enchantement autre que Solidité n'est jamais proposé/appliqué (Vitesse de Feu/Flamme/Repoussée/Loyauté/Efficacité/Fortune/etc. tous bloqués)
- [ ] Si aucun enchantement autorisé ne peut s'appliquer (objet qui finit sans rien), l'enchantement est annulé avec un message plutôt que de consommer XP/lapis pour rien
- [ ] Les livres donnés par un rôle (Cupidon Puissance IV, Chasseur, Bienfaiteur Protection II, rôles solitaires Tranchant IV) ne sont jamais bridés par ce système (ils ne passent pas par la table)
- [ ] La canne à pêche ne permet plus d'attraper quoi que ce soit (message "La pêche est désactivée." au lancer)
- [ ] Un seau de lave ne peut plus être vidé pour poser de la lave (message affiché, seau non consommé)

### Scattering et invincibilité
- [ ] Chaque joueur est téléporté à un endroit différent, sur un point de terrain sûr (pas dans le vide, pas dans l'eau/lave) — corrigé : évite maintenant les feuilles d'arbre, force le chargement du chunk, retente jusqu'à 10 fois si eau/lave — à revalider
- [🟢] Invulnérabilité active pendant les 30 premières secondes, puis désactivée automatiquement
- [🟢] `/lg stop` pendant la phase de scattering/invincibilité annule bien tout proprement (pas de bug de tâche différée qui se déclenche plus tard)
- [🟢] Chaque joueur reçoit bien un stack de 64 steaks cuits au moment du scattering
- [🟢] La vie, la faim et le feu sont bien réinitialisés au scattering (corrigé : ne l'étaient pas, un joueur pouvait démarrer la partie suivante avec la vie/faim de la fin de la précédente) — à revalider

### Révélation des rôles (10 min après le vrai début)
- [🟢] Avant la révélation : `/lg me`, `/lg regle`, `/lg sonder`, `/lg soigner`, `/lg empoisonner`, `/lg tirer` renvoient tous "les rôles n'ont pas encore été révélés"
- [ ] Avant la révélation : la corruption des loups ne progresse pas (aucun gain même en restant collé à la victime)
- [🟢] Avant la révélation : le Loup-Garou n'a **pas** la Force la nuit, la Petite Fille ne peut **pas** activer l'invisibilité en retirant son armure
- [🟢] À la révélation (10 min) : chaque joueur reçoit le message d'explication de son rôle, et le pouvoir jour/nuit s'active immédiatement si applicable (Force du Loup si c'est la nuit, etc.)
- [🟢] `/lg forcereveal` (OP) déclenche bien la révélation immédiatement et désactive le minuteur automatique (pas de double révélation à 10 min)

### PVP différé (30 min après le vrai début)
- [🟢] Avant l'activation : un coup porté entre deux joueurs de la partie est bien annulé (aucun dégât, message affiché à l'attaquant)
- [🟢] Avant l'activation : un coup PVP annulé ne déclenche **pas** le système de mort différée (pas de "mort" fantôme)
- [🟢] À 30 min : le PVP s'active automatiquement pour tout le monde
- [🟢] `/lg forcepvp` (OP) active le PVP immédiatement
- [🟢] Tant que le PVP n'est pas activé : un joueur qui subirait normalement une mort réelle (chute, lave, faim...) est soigné à pleine vie à la place ("Vous avez survécu !"), quelle que soit la cause
- [🟢] Une fois le PVP activé : la mort redevient réelle dans les mêmes conditions

### Système de mort en deux temps
- [🟢] Un coup mortel annule les dégâts, rend le joueur invulnérable, et le tue réellement 15 secondes plus tard (+ jusqu'à 10s supplémentaires si une offre de soin ou de conversion est en cours, voir sections dédiées)
- [🟢] Le message de mort (avec rôle révélé) et le kill crédité au bon joueur
- [🟢] Le respawn remet bien le joueur en mode spectateur
- [🟢] `/lg tirer` (Chasseur) fonctionne bien pendant sa propre fenêtre de mort différée

### Potion de vie de la Sorcière (offre automatique, remplace l'ancien `/lg soigner` disponible à tout moment)
- [🟢] Si une Sorcière vivante n'a pas encore utilisé sa potion de vie : à la fin de l'agonie (15s) de tout joueur qui n'est pas éligible à l'infection, la mort réelle est suspendue et la Sorcière reçoit un message cliquable "[Soigner]" pendant 10 secondes
- [🟢] En cliquant (ou via `/lg soigner <joueur>`) dans les 10s : le joueur est sauvé (revit, équipement restauré), la Sorcière gagne +1 honneur, sa potion de vie est consommée
- [🟢] Sans réaction de la Sorcière dans les 10s : la mort réelle a bien lieu normalement
- [🟢] Si aucune Sorcière n'est vivante, ou si sa potion de vie est déjà utilisée : aucune offre n'est envoyée, la mort réelle a lieu immédiatement à la fin de l'agonie
- [🟢] La Sorcière peut recevoir l'offre pour sa propre mort et se sauver elle-même (`/lg soigner <sonNom>`)
- [🟢] `/lg soigner <joueur>` échoue ("n'attend pas de décision de soin") si utilisé en dehors de cette fenêtre de 10s

### Potion de mort de la Sorcière (retire 2 cœurs définitifs, ne tue plus instantanément)
- [🟢] `/lg empoisonner <joueur>` retire bien 2 cœurs de vie maximum de façon permanente (barre de cœurs réduite, visible immédiatement) au lieu de tuer instantanément
- [🟢] Si la vie actuelle du joueur dépasse son nouveau maximum, elle est bien ramenée à ce nouveau maximum (peut tuer le joueur si sa vie était déjà très basse)
- [ ] La réduction de vie maximum persiste jusqu'à la fin de la partie (pas de régénération naturelle du plafond)
- [🟢] En fin de partie, le malus de cœurs est bien nettoyé (`PoisonManager.clearPoison`), le joueur retrouve 10 cœurs normaux au prochain lobby

### Corruption des loups et conversion (`/lg infecter` a changé de fonctionnement : il n'infecte plus la propre victime pendant l'agonie, mais accepte l'offre de conversion envoyée au Père des Loups)
- [ ] Un Loup-Garou qui reste à moins de 6 blocs d'un joueur (non-loup) le corrompt de 1% toutes les 5 secondes
- [ ] Un Père des Loups qui reste à moins de 6 blocs d'un joueur le corrompt bien plus vite : 1% par seconde
- [ ] Plusieurs loups proches de la même victime cumulent bien leurs contributions (progression plus rapide)
- [ ] La victime ne reçoit **aucun message/indicateur** de sa progression de corruption (mécanisme silencieux)
- [ ] La corruption ne redescend jamais, elle stagne juste si aucun loup n'est à proximité
- [ ] Le Loup Blanc ne participe **pas** à la corruption (solo, hors meute)
- [ ] Quand un joueur corrompu à 100% meurt de la main d'un Loup-Garou ou Père des Loups (pas un autre camp) : sa mort réelle est suspendue, et c'est le **Père des Loups** (pas la victime) qui reçoit un message cliquable dans le chat pendant 10 secondes ("Infecter" / "Laisser mourir")
- [ ] Si aucun Père des Loups n'est vivant au moment du kill : la victime meurt normalement, aucune offre n'est envoyée
- [ ] En cliquant "Infecter" (ou `/lg infecter <joueur>`) dans les 10s : la victime devient Loup-Garou, reprend sa vie/équipement, rejoint la liste des loups connus (et réciproquement), `/lg regle` reflète le nouveau rôle
- [ ] En cliquant "Laisser mourir" (ou `/lg laissermourir <joueur>`) : la mort réelle a lieu immédiatement, sans attendre la fin des 10s
- [ ] Sans réponse du Père des Loups dans les 10s : la mort réelle a bien lieu normalement (comportement par défaut)
- [ ] La victime elle-même ne reçoit aucun message pendant ce délai (silencieux jusqu'au résultat final)
- [ ] Une conversion réussie relance bien `VictoryChecker` (le camp d'origine peut perdre la partie si c'était son dernier membre)
- [ ] La corruption est bien réinitialisée en fin de partie (`/lg stop` ou victoire)

### Chat de la meute (`/lg loups <message>`, nécessite plusieurs comptes)
- [ ] Utilisable uniquement la nuit ("Ce chat n'est disponible que la nuit." le jour) et uniquement une fois les rôles révélés
- [ ] Loup-Garou, Père des Loups et Loup Blanc peuvent écrire ; tout autre rôle (y compris Petite Fille) reçoit "Vous n'avez pas accès à ce chat."
- [ ] Le message est bien reçu par tous les Loups-Garous/Père des Loups/Loup Blanc vivants
- [ ] La Petite Fille vivante reçoit aussi le message (lecture seule, elle ne peut pas répondre via `/lg loups`)
- [ ] Aucun autre joueur (Villageois, Voyante, solos autres que Loup Blanc...) ne reçoit le message
- [ ] Le pseudo de l'expéditeur n'apparaît **jamais**, même pour les autres Loups-Garous/Père des Loups (masqué à tout le monde, y compris entre membres de la meute) — le message affiche juste "[Meute] <texte>"
- [ ] Un joueur mort (loup ou Petite Fille) ne reçoit plus les messages

### Coloration de pseudo (`/lg color <joueur1> <joueur2> ...>`, nécessite plusieurs comptes)
- [ ] Utilisable par n'importe quel joueur (pas besoin d'OP), à tout moment (pas seulement en partie)
- [ ] `/lg color` sans argument (ou nom de joueur invalide) affiche un message d'erreur clair, aucun menu ne s'ouvre
- [ ] `/lg color <joueur>` ouvre un menu avec 16 laines colorées
- [ ] `/lg color <joueur1> <joueur2>` (plusieurs cibles) applique la couleur choisie aux deux joueurs en un seul clic
- [ ] Cliquer une laine colore bien le pseudo du/des joueur(s) ciblé(s) au-dessus de leur tête **et** dans la liste des joueurs (Tab)
- [ ] La couleur choisie n'est visible que par celui qui a fait `/lg color` — un autre joueur qui regarde la même cible ne voit aucun changement
- [ ] Refaire `/lg color <joueur>` avec une autre couleur remplace bien l'ancienne (le joueur n'apparaît jamais dans deux couleurs à la fois)
- [ ] La couleur reste appliquée après un cycle jour/nuit et après la fin d'une partie (`/lg stop` ou victoire) — pas de nettoyage automatique
- [ ] Se déconnecter puis se reconnecter fait perdre les couleurs précédemment choisies (limitation connue, scoreboard personnel recréé à la reconnexion)

### Rôles (un par un, en conditions réelles avec plusieurs joueurs)
- [🟢] **Villageois** : aucun comportement particulier
- [🟢] **Loup-Garou** : Force I la nuit, retirée le jour ; `/lg regle` liste bien les autres Loups-Garous/Père des Loups de la partie (pas lui-même)
- [ ] **Père des Loups** : Force I comme un loup, corrompt les autres joueurs plus vite qu'un Loup-Garou classique (voir section Corruption) ; `/lg regle` liste bien les autres loups
- [ ] **Grand Méchant Loup** : Force I en **permanence**, jour et nuit (contrairement au Loup-Garou classique qui la perd le jour) ; tous les autres aspects d'un loup normal fonctionnent (corruption, `/lg loups`, liste des loups connus, bonus de kill Speed/Absorption)
- [ ] **Bonus de kill des loups** : tuer un joueur donne Speed I + Absorption I (2♥) pendant 1 minute au tueur — vérifier pour Loup-Garou, Père des Loups, Grand Méchant Loup **et** Loup Blanc
- [ ] **Loup-Garou Craintif** : tous les aspects normaux d'un loup (corruption, `/lg loups`, liste des loups connus) — mais Force I/Faiblesse/Résistance dynamiques selon le nombre de Loups-Garous à moins de 20 blocs (lui inclus), recalculé toutes les 5 secondes
  - [ ] Plus de 4 loups à portée → Faiblesse I
  - [ ] 2 loups ou moins à portée → Résistance I le jour, Force I la nuit (aucun effet si entre 3 et 4 loups à portée)
  - [ ] Seul à portée (aucun autre loup à moins de 20 blocs) → bonus de vitesse de déplacement (Speed 0.5, custom), en plus de la Résistance/Force ci-dessus
  - [ ] Les effets changent bien en se rapprochant/éloignant d'autres loups, avec un délai de recalcul de 5 secondes maximum (pas besoin d'attendre un changement jour/nuit)
  - [ ] `/lg vote` : cliquer sur n'importe quel joueur dans l'interface de vote est toujours traité comme un vote blanc (jamais de vote enregistré sur une cible), message dédié
  - [ ] Sa mort ne produit **aucun message** de broadcast (ni "est mort, tué par...", ni "est mort !"), contrairement à tous les autres rôles
  - [ ] Corrompu à 100% et tué par un loup : **aucune offre d'infection** n'est envoyée au Père des Loups (mort réelle directe)
  - [ ] Tué alors qu'une Sorcière vivante a encore sa potion de vie : **aucune offre de soin** ne lui est envoyée (mort réelle directe)
  - [ ] Tous les effets (Faiblesse, Résistance, bonus de vitesse) sont bien nettoyés en fin de partie (`CraintifManager.clear`)
- [ ] **Loup-Garou Perfide** : tous les aspects normaux d'un loup (Force I la nuit, corruption, `/lg loups`, liste des loups connus)
  - [ ] Retirer toute son armure pendant la nuit déclenche l'invisibilité 5 minutes, 1x/nuit (comme la Petite Fille)
  - [ ] Remettre une pièce d'armure annule l'effet pour le reste de la nuit (pas de réactivation avant la nuit suivante)
  - [ ] Le jour, l'invisibilité est retirée automatiquement même si les 5 minutes ne sont pas écoulées
  - [ ] Pendant l'invisibilité, seuls la Petite Fille et le Feu Follet (vivants) voient ses particules ; les autres joueurs ne voient rien
  - [ ] Il voit aussi les particules d'une Petite Fille ou d'un Feu Follet actuellement invisible
  - [ ] Partage bien le même mécanisme que la Petite Fille (`NightInvisibilityRole`, `InvisibilityListener` généralisé — vérifier qu'aucune régression n'est apparue pour la Petite Fille elle-même)
- [ ] **Vilain Petit Loup** : tous les aspects normaux d'un loup (corruption, `/lg loups`, liste des loups connus), mais **pas** de Force I classique (ni jour, ni nuit)
  - [ ] Bonus de dégâts au corps-à-corps équivalent à Force 0.5 (+1.5 dégâts) actif en permanence, jour **et** nuit
  - [ ] Bonus de vitesse de déplacement (Speed 0.5, custom) actif uniquement la nuit, retiré au lever du jour
  - [ ] Le bonus de vitesse résiduel est bien nettoyé en fin de partie (`VilainPetitLoupManager.clear`)
- [🟢] **Petite Fille** : invisibilité 5 min en retirant toute l'armure la nuit, 1x/nuit, annulée en remettant une pièce d'armure
- [ ] **Voyante** : `/lg sonder` révèle bien rôle + équipe, seulement la nuit, 1x/nuit
- [ ] **Salvateur** : `/lg proteger <joueur>` applique Résistance I + 50% de réduction des dégâts de chute à la cible, 1x/épisode
  - [ ] Le joueur protégé ne reçoit **aucun message** l'informant de sa protection
  - [ ] La protection dure jusqu'à la fin de l'épisode courant (testable en accélérant le cycle jour/nuit) puis Résistance I est bien retirée
  - [ ] `/lg proteger` refuse une seconde utilisation dans le même épisode ("déjà utilisé votre protection cet épisode"), redevient disponible au nouvel épisode
  - [ ] En fin de partie (`/lg stop` ou victoire), la Résistance I résiduelle du joueur protégé est bien nettoyée
- [ ] **Idiot du Village** : tué par un joueur qui n'est **pas** Loup-Garou/Père des Loups (Village, Amoureux, solo...) → survit avec 8 cœurs (16 PV) au lieu de mourir, 1x/partie
  - [ ] Un broadcast annonce publiquement son rôle au moment de la survie (message dédié, pas le message de mort classique)
  - [ ] Aucun message/kill/mort n'est enregistré pour cette "fausse mort" (pas de `PlayerDeathEvent`, pas de crédit de kill au tueur)
  - [ ] Tué une seconde fois par un non-Loup (pouvoir déjà consommé) : meurt normalement cette fois
  - [ ] Tué par un Loup-Garou ou un Père des Loups (même la première fois) : meurt normalement, pas de survie
  - [ ] Priorité correcte si plusieurs mécanismes de mort différée pourraient s'appliquer en même temps (corruption/infection, offre de la Sorcière) — l'auto-survie de l'Idiot passe avant l'offre de la Sorcière
- [ ] **Ancien** : Résistance 0.5 permanente (-10% de tous les dégâts subis, testable en comparant les PV perdus avec/sans le rôle)
  - [ ] Tué par un Loup-Garou ou un Père des Loups : ressuscite avec sa vie **actuelle** (pas de valeur fixe, comme la Sorcière), 1x/partie, et perd définitivement sa Résistance 0.5 (vérifier que les dégâts ne sont plus réduits après)
  - [ ] Tué une seconde fois par un Loup (pouvoir déjà consommé) : meurt normalement
  - [ ] Tué par un villageois (ni Loup, ni solitaire — donc Village ou Amoureux) : meurt normalement (pas de résurrection), et le tueur perd la moitié de sa vie maximale, définitivement (arrondi au cœur supérieur, ex: 11♥→6♥)
  - [ ] Tué par un solitaire (camp NEUTRAL) : meurt normalement, aucun effet particulier ni pour lui ni pour le tueur
  - [ ] Le malus de vie maximale du tueur "villageois" est bien nettoyé en fin de partie (`AncienManager.clear`)
- [ ] **Bienfaiteur** : reçoit 2 livres Protection II à la révélation
  - [ ] `/lg conferer <joueur>` refuse de se cibler soi-même et refuse un joueur déjà ciblé précédemment
  - [ ] Le joueur ciblé ne reçoit **aucun effet immédiat** ; le don (+1 cœur permanent, +1 cœur de vie actuelle) arrive exactement 3 minutes après la commande, avec un message dédié
  - [ ] `/lg conferer` refuse une nouvelle utilisation avant 5 minutes depuis la dernière ("vous devez encore attendre... secondes")
  - [ ] Après le 3ᵉ don utilisé (peu importe si les dons sont déjà délivrés ou encore en attente), `/lg conferer` refuse toute utilisation supplémentaire ("déjà offert vos 3 dons") et le Bienfaiteur reçoit une régénération lente permanente (+1 cœur par minute)
  - [ ] La régénération lente s'arrête bien à sa mort et ne fuit pas après la fin de la partie (`BienfaiteurRole.onDeath`/`cancelRegen` dans `GameEnder`)
  - [ ] Si le joueur ciblé meurt avant la délivrance (3 min), le don est silencieusement annulé (pas d'erreur, pas de cœur donné à un mort)
  - [ ] Le cœur permanent offert est bien nettoyé en fin de partie (`BienfaiteurManager.clear`)
- [🟢] **Sorcière** : `/lg soigner` (offre automatique à 10s) et `/lg empoisonner` (2 cœurs définitifs), chacun 1x/partie
- [🟢] **Chasseur** : reçoit un arc Puissance IV + 64 flèches à la révélation
  - [ ] Bonus de dégâts contre le camp des Loups-Garous : commence à Force 0.5 (+1.5 dégâts au corps-à-corps uniquement, pas à l'arc), augmente de 0.1 par Loup-Garou/Père des Loups tué, plafonné à Force I (+3 dégâts)
  - [ ] `/lg tirer <joueur>` pendant sa propre agonie, 1x/partie, refuse de cibler son propre tueur ("vous ne pouvez pas tirer sur votre propre tueur")
  - [ ] Sur un Loup-Garou/Père des Loups : sa vie **maximale** est réduite de moitié et arrondie au cœur supérieur si besoin (10♥→5♥, 12♥→6♥, 11♥→6♥), effet permanent, potentiellement lié à une chute de vie actuelle si elle dépassait le nouveau maximum
  - [ ] Sur un solitaire (camp NEUTRAL) : perd un quart de sa vie maximale (arrondi au cœur supérieur), effet permanent
  - [ ] Sur un Villageois/Amoureux : perd 6 cœurs de vie **actuelle** seulement (non définitif, pas de mort instantanée garantie)
  - [ ] Le malus de vie maximale (Loup/solitaire) est bien nettoyé en fin de partie (`ChasseurShotManager.clear`)
  -[ ] Si un Loup-Garou ou un Père des Loups tue le Chasseur, il ne reçoit **pas** le bonus Speed I + Absorption I habituel (le Loup Blanc, lui, le reçoit normalement)
- [ ] **Cupidon** : reçoit un arc simple + livre Puissance IV + Punch I + 64 flèches **à la révélation (10 min)**, pas au scattering ; `/lg lier <joueur1> <joueur2>` fonctionne, 1x/partie
- [🟢] **Chasseur de Primes** : reçoit un livre Tranchant IV **à la révélation (10 min)**, pas au scattering
  - [🟢] Aucun contrat avant l'activation du PVP (`/lg regle` dit "Aucun contrat actif")
  - [ ] Premier contrat reçu **à l'activation du PVP** (testable via `/lg forcepvp`, pas besoin d'attendre 30 min)
  - [ ] Tuer la cible du 1er contrat donne un arc Puissance IV + 64 flèches et marque le contrat "rempli"
  - [ ] Tuer la cible du 2e contrat donne des bottes en diamant Chute Amortie III
  - [ ] Le second contrat n'arrive **pas** tout de suite après le premier, mais au **lever du jour suivant** (nouvel épisode)
  - [ ] Si la cible du contrat en cours meurt d'une autre main (PVP, chute...), le contrat est annulé (message au Chasseur de Primes) et le contrat suivant arrive au lever du jour suivant, comme pour un succès
  - [ ] Une fois les 2 contrats résolus (remplis ou annulés), `/lg regle` affiche "Vous n'avez plus de contrat à accomplir"
- [🟢] **Loup Blanc** : reçoit Force I la nuit comme un loup ; `/lg regle` liste bien les Loups-Garous/Père des Loups de la partie ; reçoit le livre Tranchant IV comme tout solo ; camp `Solitaire` dans le scoreboard (pas `Loups`) ; gagne seul même si tous les autres morts sont des loups
  - [🟢] Obtient bien 15 cœurs (30 PV) au moment de la révélation des rôles, plein de vie à cet instant
  - [🟢] Le bonus de cœurs est bien nettoyé en fin de partie (`/lg stop` ou victoire), retour à 10 cœurs normaux au lobby
- [🟢] **Ange** : reçoit le livre Tranchant IV comme tout solo ; à la révélation, `/lg regle` invite à choisir sa forme
  - [ ] `/lg ange dechu` : passe à 12 cœurs, une cible vivante aléatoire est assignée (rôle révélé dans le message et via `/lg regle`)
  - [ ] Tuer sa cible (crédité comme tueur) fait passer l'Ange Déchu à 15 cœurs (message dédié) ; gagne seul
  - [ ] `/lg ange gardien` : passe à 15 cœurs, un protégé vivant aléatoire est assigné (rôle révélé) ; l'Ange rejoint le camp effectif de son protégé (`teamOverride`, vérifier le scoreboard)
  - [ ] Si le protégé meurt : l'Ange Gardien passe à 12 cœurs + Faiblesse I permanente, redevient solitaire (`teamOverride` retiré), doit désormais gagner seul
  - [ ] `/lg regen` : utilisable seulement en forme Gardien, seulement si le protégé est sous 4 cœurs et vivant, 1x/partie — donne Régénération I 1 minute au protégé
  - [ ] Impossible de choisir une forme deux fois, ou avant la révélation des rôles
  - [ ] En fin de partie, le bonus/malus de cœurs et la Faiblesse sont bien nettoyés (`AngeManager.clearHearts`)

### Cupidon / camp Amoureux (nécessite au moins 3 comptes) — comportement revu : le camp Amoureux se forme désormais systématiquement, peu importe que les deux liés soient du même camp ou de camps opposés
- [ ] Lien entre deux joueurs, **même camp ou camps opposés** : les deux amoureux ET le Cupidon rejoignent systématiquement le nouveau camp "Amoureux" (vérifier le "Groupe" dans le scoreboard des 3 joueurs)
- [ ] Si l'un des deux amoureux meurt (n'importe quelle cause), l'autre meurt instantanément de chagrin (message dédié) — garantit qu'ils ne peuvent gagner qu'ensemble
- [ ] Le camp Amoureux gagne s'il est le dernier camp survivant (message de victoire dédié) ; le Cupidon n'a pas besoin de survivre pour que les deux amoureux gagnent
- [ ] Si les deux amoureux meurent tous les deux, le Cupidon redevient un simple Villageois (vérifier `/lg me` et le scoreboard)
- [ ] Le lien amoureux ne survit pas d'une partie à l'autre (reset propre via `/lg stop` ou victoire)

### Rôles solo (camp NEUTRAL)
- [ ] Tout rôle solo (ex: Chasseur de Primes) reçoit à la révélation un livre Tranchant IV, pas de bow/livre Cupidon
- [ ] Un solitaire gagne **seul** : il doit être l'unique joueur restant (Village, Loups, Amoureux et tous les autres solitaires éliminés) — message de victoire nominatif
- [ ] Avec plusieurs joueurs solo en même temps : ils doivent s'éliminer entre eux aussi, la partie ne se termine pas tant qu'il en reste plus d'un
- [ ] **Feu Follet** : reçoit le livre Tranchant IV (comme tout solo) + une Plume nommée "§bPlume du Feu Follet"
  - [ ] Retirer toute son armure à n'importe quel moment (jour ou nuit) déclenche l'invisibilité **permanente**, sans limite de durée ni de fréquence (contrairement à la Petite Fille)
  - [ ] Remettre au moins une pièce d'armure annule immédiatement l'invisibilité
  - [ ] Si déjà sans armure au moment de la révélation, l'invisibilité s'active tout de suite (pas besoin de retirer/remettre une pièce pour déclencher l'événement)
  - [ ] Pendant l'invisibilité, seuls les joueurs Petite Fille (vivants) voient des particules à sa position ; les autres joueurs ne voient rien
  - [ ] Le Feu Follet voit aussi les particules d'une Petite Fille actuellement invisible (grâce à son propre pouvoir)
  - [ ] `/lg folie` : active Speed I pendant 1 minute et met le feu (5s) à tout joueur frappé au corps-à-corps pendant cette minute ; n'affecte pas les dégâts à l'arc/projectile ; 1x/10 minutes réelles
  - [ ] Clic droit avec la Plume : téléporte 50 blocs dans la direction visée (s'arrête avant un mur/obstacle rencontré en chemin), 1x/10 minutes réelles, message si pas encore rechargée
  - [ ] Chaque début de nuit : révèle le rôle d'un joueur aléatoire à moins de 50 blocs (message privé) ; si personne à portée, message dédié et aucune info cette nuit-là
  - [ ] Tous les effets (Invisibilité, Speed) sont bien nettoyés en fin de partie, pas de fuite après `/lg stop`
- [ ] **Imitateur** : Force I permanente (jour et nuit) dès la révélation
  - [ ] La Force I disparaît automatiquement au milieu de l'épisode 6 (transition vers la nuit de l'épisode 6), sans avoir besoin de tuer qui que ce soit
  - [ ] La première victime qu'il tue (peu importe le camp) lui transmet son rôle : `/lg me` et `/lg regle` affichent le nouveau rôle juste après le kill, message dédié reçu
  - [ ] Le rôle copié est une version **neuve** (charges/cooldowns non consommés), pas l'état exact de la victime
  - [ ] Il reçoit immédiatement les objets associés au rôle copié (arc du Chasseur, livres du Cupidon/Bienfaiteur, etc.) et le message d'explication du nouveau rôle
  - [ ] La Force I est immédiatement retirée dès qu'il imite un rôle (même si le milieu de l'épisode 6 n'est pas encore atteint)
  - [ ] Un second kill après avoir déjà imité un rôle **ne déclenche pas** une seconde imitation (il n'est plus Imitateur, désormais un rôle normal)
  - [ ] **Copie d'un Loup-Garou/Père des Loups** : rejoint la liste des loups connus (dans les deux sens), a accès à `/lg loups`, mais son camp reste verrouillé Solitaire (pas de victoire Loups) — vérifier le scoreboard
  - [ ] **Copie du Loup Blanc** : reçoit les 15 cœurs de vie et la liste des Loups-Garous connus, reste Solitaire
  - [ ] **Copie du Chasseur de Primes** : reçoit immédiatement un premier contrat (puisque les contrats normaux ne se déclenchent qu'à l'activation du PVP ou au changement d'épisode)
  - [ ] **Copie du Cupidon, cas 1 (aucun lien fait par la victime)** : devient Cupidon normal avec `/lg lier` disponible, camp non verrouillé (VILLAGE tant qu'il n'a pas lié personne)
  - [ ] **Copie du Cupidon, cas 2 (victime avait déjà lié 2 joueurs)** : devient immédiatement membre du camp Amoureux existant, sa potion de lien est déjà consommée, il remplace la victime comme "Cupidon" du couple (si les deux amoureux meurent plus tard, c'est lui qui redeviendrait Villageois)
  - [ ] **Copie du Cupidon, cas 3 (les deux amoureux de la victime étaient déjà morts)** : la victime était déjà redevenue Villageois avant sa mort, donc l'Imitateur copie simplement Villageois (comportement automatique, rien de spécial à coder/tester à part vérifier que ça tombe bien sur Villageois)
  - [ ] **Copie de l'Ange** : reste Solitaire par défaut (Déchu) ; s'il choisit ensuite Gardien via `/lg ange`, son camp change bien pour rejoindre son protégé (pas de verrouillage forcé pour ce rôle précis, contrairement aux autres)
  - [ ] Pour tous les autres rôles (Villageois, Sorcière, Voyante, Petite Fille, Salvateur, Ancien, Idiot du Village, Bienfaiteur) : camp verrouillé Solitaire en permanence malgré le nouveau rôle
- [ ] **Joueur de Flûte** : reçoit le livre Tranchant IV (comme tout solo), pas de Flûte de départ (doit la fabriquer)
  - [ ] Craft d'une Flûte : 8 lingots d'or autour d'un bâton (recette 3x3) → 1 Corne de Chèvre nommée "§6Flûte"
  - [ ] Tant qu'il porte une Flûte dans son inventaire (pas besoin de l'avoir en main), charme tous les joueurs vivants à moins de 20 blocs à 1% toutes les 4 secondes (aucune info donnée aux charmés, mécanisme silencieux)
  - [ ] Sans Flûte dans l'inventaire, le charme passif s'arrête immédiatement
  - [ ] Frapper un joueur au corps-à-corps donne +10% de charme instantané, une seule fois par joueur (un second coup sur le même joueur ne fait rien)
  - [ ] Clic droit avec une Flûte en main + un joueur visé à moins de 5 blocs (ligne de mire) : lui donne une Flûte (consomme 1 Flûte de son inventaire), message de confirmation à lui uniquement
  - [ ] Impossible de donner une seconde Flûte à un joueur qui en a déjà reçu une
  - [ ] Le joueur ayant reçu une Flûte ne peut pas la jeter (`/drop` ou touche Q annulés avec message)
  - [ ] Ce joueur charme désormais aussi les autres à son insu, à moitié vitesse (1% toutes les 8 secondes) — aucun message immédiat
  - [ ] Il est notifié de la présence de cette Flûte mystérieuse dans son inventaire seulement au **début de l'épisode suivant** (pas immédiatement)
  - [ ] Une Flûte ne charme jamais son propre porteur
  - [ ] Paliers de joueurs charmés à 100% (comptent même après leur mort, donc définitifs une fois atteints) :
    - [ ] 3 charmés → +1 cœur de vie permanent, message dédié
    - [ ] 6 charmés → bonus de dégâts au corps-à-corps équivalent à Force 0.5 (+1.5 dégâts, pas d'effet visuel de potion)
    - [ ] 9 charmés → passe à Force I (vraie potion, remplace le bonus 0.5) + bonus de vitesse de déplacement équivalent à Speed 0.5 (attribut, pas de potion visible)
    - [ ] 12 charmés → +1 cœur de vie permanent supplémentaire (cumulé avec celui du palier 3)
    - [ ] Tous les joueurs vivants sont charmés (minimum 6 joueurs vivants dans la partie) → Résistance I (vraie potion)
  - [ ] Tous les bonus (cœurs, vitesse) sont bien nettoyés en fin de partie (`CharmManager.clear`)

### Conditions de victoire
- [🟢] Village gagne quand tous les Loups (et solos) sont morts — message de victoire + partie `FINISHED`
- [ ] Loups gagnent quand tous les Villageois (et solos) sont morts — message de victoire + partie `FINISHED`
- [ ] Vérifier qu'une conversion par corruption qui fait basculer l'équilibre déclenche bien la victoire immédiatement
- [ ] Les Amoureux (camps opposés) gagnent quand ils sont les seuls survivants (voir section Cupidon)
- [ ] Les solitaires gagnent quand ils sont les seuls survivants (voir section Rôles solo)

### Autosmelting et diamants
- [🟢] Fer/or/cuivre bruts minés donnent directement le lingot
- [🟢] Un outil Silk Touch sur un minerai ne déclenche **pas** l'autosmelt (le bloc de minerai reste intact)
- [🟢] Les débris antiques donnent directement un éclat de netherite
- [🟢] Le compteur de diamants (`X/17` dans le scoreboard) s'incrémente correctement, y compris avec Fortune

### Système de vote (démarre 45 min après le vrai début, dure 3 épisodes)
- [🟢] Les 4 maisons de vote apparaissent bien autour du centre de la bordure (à ~150 blocs), chacune avec une enclume et un jukebox
- [ ] Les maisons/jukebox/enclumes ne peuvent pas être détruits, y compris pendant le scattering/l'invincibilité (corrigé : la protection ne s'activait qu'en `DAY`/`NIGHT`, pas avant) — à revalider
- [🟢] Clic droit sur un jukebox **avant** l'ouverture du vote : message "le vote n'est pas ouvert", pas de menu
- [🟢] Clic droit sur un jukebox **pendant** le vote : ouvre un menu avec une tête par joueur vivant (sauf soi-même) + un item "Passer"
- [🟢] Voter pour un joueur, repasser dans le jukebox et changer son vote : le dernier choix est bien celui pris en compte
- [ ] À la fin de chaque épisode de vote : honneur +1 pour ceux qui ont voté, inchangé pour ceux qui ont passé, -1 pour ceux qui n'ont rien fait (Villageois et Loups traités pareil)
- [ ] Le joueur le plus voté voit son vrai rôle apparaître dans le chat, mélangé avec 2 autres noms de rôles au hasard (sans indiquer lequel est le vrai)
- [ ] En cas d'égalité au nombre de votes, un des joueurs à égalité est choisi (pas de crash)
- [ ] Après 3 épisodes de vote, le vote se désactive (jukebox redevient inactif)
- [🟢] Le vote et les votes en cours sont bien réinitialisés entre deux parties

### Honneur (au-delà du vote)
- [🟢] Sorcière qui soigne quelqu'un (`/lg soigner`) gagne +1 honneur
- [ ] Un joueur qui tue un allié de son propre camp (Village tue Village, ou Loup tue Loup) perd -1 honneur
- [🟢] À +3 d'honneur : Villageois gagne un cœur, Loup en perd un ; à -3 c'est l'inverse (vérifier via `/lg regle` ou en comptant les cœurs à l'écran)
- [🟢] L'effet de cœur disparaît si l'honneur redescend sous le seuil (pas un bonus figé)
- [🟢] L'honneur et l'effet de cœur sont bien réinitialisés entre deux parties

### Fin de partie / spawn du lobby
- [🟢] Après une victoire ou un `/lg stop`, tous les joueurs atterrissent bien à cet endroit précis (pas dans un bloc, pas dans le vide)
- [🟢] Le spawn configuré persiste après un redémarrage du serveur (relire `config.yml`)
- [🟢] Tant que `/lg lobbyspawn` n'a jamais été utilisé, ça retombe sur l'ancien comportement (`Bukkit.getWorlds().get(0).getSpawnLocation()`)

### Scoreboard
- [🟢] Toutes les lignes s'affichent et se mettent à jour correctement : durée, cycle, épisode, groupe, joueurs vivants/total, bordure, kills, diamants
- [🟢] L'épisode s'incrémente à chaque lever du jour
- [🟢] Le "Groupe" affiché est personnel à chaque joueur (Village/Loups/Solitaire) et vide/"-" avant la révélation
- [ ] La ligne "Bordure" ne garde plus l'ancienne valeur de la partie précédente une fois revenu au lobby (corrigé : la référence au monde de partie est libérée en fin de partie) — à revalider

### Performance
- [ ] La RAM du serveur reste stable sur plusieurs parties d'affilée (corrigé : `ScoreboardManager` recréait un `Scoreboard` complet chaque seconde pour chaque joueur, jamais libéré par Bukkit — un seul scoreboard est maintenant réutilisé par joueur) — à revalider sur une session longue avec plusieurs parties

---

## Notes

- Chaque partie utilise désormais un monde avec un nom unique (suffixe timestamp) au lieu de réutiliser/supprimer le même dossier — ne rien construire dedans entre deux tests, l'ancien dossier est nettoyé au mieux (best-effort) à la génération du suivant.
- Il faut relancer `./gradlew build` et redéployer le `.jar` avant de tester, sinon le serveur tourne avec une version obsolète du plugin.
- Beaucoup de ces tests nécessitent plusieurs comptes Minecraft connectés simultanément (scattering, PVP, sonder, corruption des loups...) — les commandes de debug OP permettent de contourner certains délais mais pas le besoin d'avoir plusieurs joueurs pour les interactions à deux.


## Bugs corrigés cette session — à revalider en jeu

- Maisons de vote bloquées par du feuillage à l'intérieur, impossible à casser → `buildVoteHouse` vide maintenant explicitement tout l'intérieur à la construction au lieu de laisser ce qu'il y avait déjà (arbre compris).
- Double message "le vote n'est pas ouvert actuellement" au clic sur le jukebox → `PlayerInteractEvent` se déclenchait une fois par main (principale + secondaire), filtré sur la main principale uniquement.
- Après une 2ᵉ partie : plus de dégâts reçus/infligés, touche espace bloquée, jusqu'à quitter/rejoindre le serveur → un joueur encore en agonie (ou avec une offre de soin/infection en cours) au moment où la partie se termine restait marqué "en train de mourir" pour de bon. `DeathManager` nettoie maintenant systématiquement tout état en cours à la fin de partie (`resetAll()`).
- Effet de Force (ou autre effet de rôle : Vitesse, Absorption...) conservé d'une partie précédente (ex: Ange qui garde la Force du Loup-Garou joué avant) → ces effets n'étaient jamais retirés en fin de partie si encore actifs (partie terminée en pleine nuit). `GameEnder` les retire tous désormais.
- Scoreboard (ligne Bordure) qui ne se resetait plus → voir section Scoreboard ci-dessus.
- RAM qui se remplit après plusieurs parties → voir section Performance ci-dessus (fuite du `Scoreboard` recréé chaque seconde ; amélioration significative, mais pas garanti à 100%, voir section "Toujours ouvert").
- Maisons de vote/jukebox destructibles pendant le scattering/l'invincibilité → la protection anti-casse ne s'activait qu'en `DAY`/`NIGHT`, elle couvre maintenant toute la durée de la partie (dès `SCATTERING`).
- Vie/faim/feu non réinitialisés au scattering → un joueur démarrait la partie suivante avec l'état de fin de la précédente. Reset explicite ajouté.
- Monde qui semblait ne pas se reset (constructions/objets d'une partie précédente retrouvés) → chaque partie utilise maintenant un nom de monde unique (timestamp) au lieu de réutiliser le même dossier, donc plus besoin de compter sur la suppression de l'ancien pour avoir un monde vierge.
- Maisons de vote trouvées empilées → même cause que le point précédent (ancien dossier pas supprimé, donc ancienne maison de vote encore présente à une hauteur différente de la nouvelle) : plus possible avec un monde toujours neuf.

## Clarifié — pas un bug

- Le 1er contrat du Chasseur de Primes n'apparaît pas au `/lg forcepvp` en solo : `BountyManager` a besoin d'une autre cible vivante que lui-même, donc aucun contrat n'est possible à un seul joueur. Se testera avec un 2ᵉ compte.

## Toujours ouvert — pas encore corrigé

- RAM qui augmente encore un peu à chaque nouvelle partie (mais moins qu'avant) : probablement lié à la création d'un nouveau monde Bukkit à chaque partie plutôt qu'à la réutilisation du même (peut ne jamais être totalement à zéro par nature, chaque monde étant désormais unique). Pas de fix supplémentaire identifié pour l'instant, à surveiller sur une session très longue.
- Touche Échap qui ne répond pas parfois, en jeu normal (pas dans un menu ni pendant l'agonie) : la touche Échap est gérée entièrement côté client, un plugin serveur ne peut pas l'intercepter. C'est très probablement un symptôme de lag/gel ponctuel du serveur (la génération du monde peut déjà geler le serveur 1-2 secondes, voir Notes) plutôt qu'un bug de code — à confirmer si ça coïncide avec un `/lg start`/génération de monde ou une autre action lourde.