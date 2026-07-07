# Manuel du joueur — Loup-Garou UHC

Bienvenue ! Ce plugin recrée le mode **Loup-Garou en UHC** : une partie de survie en PVP libre où chacun a un rôle secret, et où le Village doit démasquer les Loups avant qu'ils ne l'éliminent — pendant que certains joueurs solitaires jouent leur propre partie, seuls contre tous.

## Rejoindre une partie

- `/lg join` — s'inscrire pour la prochaine partie
- `/lg leave` — annuler son inscription
- `/lg info` — voir l'état de la partie en cours
- Il faut un minimum de joueurs inscrits (3 par défaut) pour que l'**Hôte** puisse lancer la partie avec `/lg start`

> Une seule personne peut être **Hôte** à la fois sur le serveur — c'est elle qui lance/arrête la partie et configure les paramètres. Si tu es éligible, tu recevras un message cliquable en te connectant pour le devenir, ou tu peux taper `/lg host claim`.

## Déroulé d'une partie

1. **Dispersion** — chaque joueur inscrit est téléporté à un endroit différent de la carte, avec de quoi manger.
2. **Invincibilité** (3 min par défaut) — personne ne peut être blessé, le temps que tout le monde s'installe.
3. **Phase de farm** — les rôles ne sont pas encore connus, aucun pouvoir n'est actif. C'est le moment de récolter des ressources et de se préparer, comme dans un UHC classique.
4. **Révélation des rôles** (10 min après le vrai début) — chacun découvre son rôle en privé, reçoit ses éventuels objets, et les pouvoirs s'activent.
5. **Activation du PVP** (30 min après le début) — les combats commencent à compter pour de vrai. Avant ça, même un coup normalement mortel ne fait que soigner ("Vous avez survécu !").
6. **Ouverture du vote** (45 min après le début, dure 3 jours) — chaque jour, les Villageois et les Loups vivants (pas les solitaires/Amoureux) peuvent désigner quelqu'un dans l'une des 4 maisons de vote de la carte. La personne la plus désignée voit son rôle révélé publiquement (mêlé à deux faux rôles pour brouiller les pistes) — **elle n'est ni tuée ni bannie**, c'est juste de l'information pour la suite.
7. **Fin de partie** — dès qu'un camp remplit sa condition de victoire (voir plus bas).

> **Mode rapide** : l'Hôte peut activer un mode qui saute la phase de farm — chacun démarre déjà équipé, les rôles sont révélés immédiatement, et le PVP + le vote s'activent en même temps (10 min par défaut). Demande à ton Hôte si c'est actif avant de commencer !

### Si vous recevez un coup normalement mortel

Vous ne mourez pas instantanément : vous devenez un spectateur invisible pendant **15 secondes** ("vous agonisez..."). Pendant ce délai, une Sorcière peut vous sauver, ou — si vous étiez corrompu à 100% par les Loups et tué par l'un d'eux — le Père des Loups peut vous transformer en Loup-Garou au lieu de vous laisser mourir. Sans intervention, la mort est définitive à la fin du délai, et votre équipement tombe à l'endroit où vous êtes tombé.

## Les camps et conditions de victoire

| Camp | Gagne quand... |
|---|---|
| **Village** | Tous les Loups, solitaires et Amoureux sont éliminés |
| **Loups** | Tout le Village, les solitaires et les Amoureux sont éliminés |
| **Solitaire** | Il est l'unique survivant parmi les solitaires, et tous les autres camps sont éliminés |
| **Amoureux** | Les deux amoureux liés par Cupidon sont les seuls survivants |

## Les rôles

### 🏘️ Village (doit démasquer et éliminer les Loups)

| Rôle | Pouvoir |
|---|---|
| **Villageois** | Aucun pouvoir spécial — sa force, c'est le nombre. |
| **Petite Fille** | Peut devenir invisible 5 minutes en retirant toute son armure, une fois par nuit. |
| **Voyante** | `/lg sonder <joueur>` révèle le rôle et le camp d'un joueur, une fois par cycle jour/nuit. |
| **Salvateur** | `/lg proteger <joueur>` protège quelqu'un jusqu'au prochain lever du jour (résistance aux dégâts + chutes amorties), une fois par épisode. La cible n'est jamais prévenue. |
| **Idiot du Village** | S'il est tué par un non-Loup, survit automatiquement une fois (son rôle est alors révélé à tous). Face à un vrai Loup, il meurt normalement. |
| **Ancien** | Résiste un peu mieux aux dégâts en permanence. Tué par un Loup, il ressuscite une fois (mais perd cet avantage). Tué par un Villageois, le responsable perd la moitié de sa vie maximale. |
| **Bienfaiteur** | Peut offrir discrètement +1 cœur permanent à 3 joueurs différents au cours de la partie (`/lg conferer <joueur>`), livré 3 minutes plus tard sans que la cible le sache. Une fois les 3 dons faits, il régénère lentement en continu. |
| **Sorcière** | Peut sauver un joueur mourant une fois par partie (`/lg soigner`), et empoisonner quelqu'un pour lui retirer 2 cœurs définitivement une fois (`/lg empoisonner`). |
| **Chasseur** | Frappe plus fort au corps-à-corps contre les Loups. En mourant, dispose de 15 secondes pour une dernière riposte (`/lg tirer`) sur un joueur autre que son tueur. |
| **Cupidon** | Lie deux joueurs par l'amour (`/lg lier`, une fois) — ils forment alors le camp des Amoureux et ne peuvent gagner qu'ensemble. Si l'un meurt, l'autre meurt de chagrin. |

### 🐺 Loups (doivent éliminer le Village)

| Rôle | Pouvoir |
|---|---|
| **Loup-Garou** | Force au corps-à-corps la nuit. Connaît l'identité des autres Loups. Corrompt silencieusement les joueurs à proximité. |
| **Père des Loups** | Comme le Loup-Garou, mais corrompt bien plus vite — et c'est lui qui reçoit l'offre de transformer une victime corrompue à 100% en Loup-Garou plutôt que de la laisser mourir. |
| **Grand Méchant Loup** | Comme le Loup-Garou, mais garde sa force en permanence, jour et nuit. |
| **Loup-Garou Craintif** | Ses effets (force, faiblesse, résistance, vitesse) changent automatiquement selon le nombre de Loups à moins de 20 blocs. Ne peut voter que blanc, et sa mort ne prévient jamais personne. |
| **Loup-Garou Perfide** | Comme le Loup-Garou, mais peut aussi devenir invisible la nuit en retirant son armure, comme la Petite Fille. |
| **Vilain Petit Loup** | Comme le Loup-Garou, mais son bonus de force est plus faible et permanent (jour et nuit), avec un bonus de vitesse la nuit. |

### 🌙 Solitaires (chacun gagne seul, sauf mention contraire)

| Rôle | Pouvoir |
|---|---|
| **Chasseur de Primes** | Reçoit des contrats secrets pour éliminer une cible précise, récompensés en équipement à chaque contrat rempli. |
| **Loup Blanc** | Combat comme un Loup et connaît leur identité, mais doit tous les éliminer — Loups compris — pour gagner. Beaucoup de vie. |
| **Ange** | Choisit d'être **Déchu** (doit éliminer une cible précise et gagner seul) ou **Gardien** (rejoint le camp d'un protégé assigné au hasard et gagne avec lui, peut le soigner une fois si besoin). |
| **Feu Follet** | Invisibilité permanente (sans limite de temps) en retirant son armure. Peut mettre le feu au corps-à-corps temporairement, se téléporter avec sa Plume, et découvre chaque nuit le rôle d'un joueur proche. |
| **Imitateur** | Fort au combat en début de partie. En tuant sa première victime, vole réellement son rôle et ses pouvoirs. |
| **Joueur de Flûte** | Doit fabriquer une flûte (8 lingots d'or + un bâton). Charme silencieusement les joueurs proches, et peut confier des flûtes à d'autres qui charment à leur tour sans le savoir. Débloque des bonus permanents selon le nombre de joueurs charmés à fond. |

## Le système d'honneur

Chaque joueur a une jauge d'honneur **individuelle et invisible**, allant de -3 à +3 (elle démarre à 0 à chaque partie). Personne ne peut voir la vôtre ni celle des autres, mais elle influence discrètement vos cœurs de vie maximum et votre vitesse de déplacement si vous atteignez certains paliers.

**Comment elle évolue :**

| Action | Effet |
|---|---|
| Voter (n'importe quel choix, y compris passer volontairement dans l'interface de vote) | +1 |
| Ne pas voter du tout pendant que le vote est ouvert | -1 |
| La Sorcière soigne quelqu'un avec sa potion de vie | +1 (pour la Sorcière) |
| Tuer un joueur de **votre propre camp effectif** (trahison, tir ami — y compris un Amoureux tuant l'autre) | Toujours défavorable pour le tueur : -1 pour tout le monde, **sauf pour un Loup qui tue un autre Loup, qui prend +1** (trahir la meute est puni, jamais avantageux) |

**Aux extrêmes (±2 et ±3), les effets s'additionnent en avançant vers le palier suivant :**

| Palier | Village | Loups |
|---|---|---|
| **+2** | Bonus de vitesse de déplacement | Malus de vitesse de déplacement |
| **+3** | +1 cœur de vie maximum (en plus du bonus de vitesse) | -1 cœur de vie maximum (en plus du malus de vitesse) |
| **-2** | Malus de vitesse de déplacement | Bonus de vitesse de déplacement |
| **-3** | -1 cœur de vie maximum (en plus du malus de vitesse) | +1 cœur de vie maximum (en plus du bonus de vitesse) |

Les Amoureux et les rôles solitaires ne sont concernés par aucun de ces effets. La jauge (et ses éventuels effets) est remise à zéro à chaque nouvelle partie.

> En clair : pour le Village, voter systématiquement (même pour passer) et ne jamais trahir un allié est bénéfique sur la durée. Pour les Loups, avoir un honneur bas peut être intéressant — mais **attention**, trahir un autre Loup ne vous y aide pas, ça vous en éloigne au contraire. Et impossible de savoir où vous en êtes sans faire le calcul vous-même !

## Commandes utiles

| Commande | Utilité |
|---|---|
| `/lg help` | Liste des commandes |
| `/lg info` | État de la partie en cours |
| `/lg join` / `/lg leave` | S'inscrire / se désinscrire |
| `/lg me` | Voir son propre rôle |
| `/lg regle` | Réafficher l'explication de son rôle |
| `/lg color <joueur1> <joueur2> ...>` | Colore le pseudo d'un ou plusieurs joueurs (au-dessus de la tête + liste Tab) — **visible uniquement par vous**, à tout moment |
| `/lg host claim` / `/lg host release` | Revendiquer ou quitter le rôle d'Hôte (si vous êtes éligible) |

Les commandes de pouvoir de rôle (`/lg sonder`, `/lg soigner`, `/lg tirer`, `/lg lier`, etc.) sont listées dans le message d'explication de votre rôle (`/lg regle`) une fois les rôles révélés.

## Astuces

- Les offres de sauvetage/infection (Sorcière, Père des Loups) apparaissent en jeu sous forme de message cliquable — si vous ne pouvez pas cliquer (ex: Bedrock), la commande complète est toujours indiquée en dessous.
- `/lg color` est purement personnel : coloriez qui vous voulez, personne d'autre ne verra votre choix.
