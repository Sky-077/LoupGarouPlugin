const path = require("path");
require("dotenv").config({ path: path.join(__dirname, "..", ".env") });
const { REST, Routes, SlashCommandBuilder } = require("discord.js");
const roles = require("./roles");
const { CATEGORIES } = require("./tickets");

const { DISCORD_TOKEN, CLIENT_ID, GUILD_ID } = process.env;

if (!DISCORD_TOKEN || !CLIENT_ID) {
    console.error("DISCORD_TOKEN et CLIENT_ID sont requis dans .env");
    process.exit(1);
}

const roleCommands = roles.map((role) =>
    new SlashCommandBuilder()
        .setName(role.command)
        .setDescription(`Affiche les infos du rôle ${role.name}`)
        .toJSON()
);

const ticketCommands = Object.entries(CATEGORIES).map(([command, category]) =>
    new SlashCommandBuilder()
        .setName(command)
        .setDescription(`Envoyer ${category === "Bug" ? "un bug" : `une ${category.toLowerCase()}`} au staff en privé`)
        .toJSON()
);

const commands = [...roleCommands, ...ticketCommands];

const rest = new REST().setToken(DISCORD_TOKEN);

(async () => {
    const target = GUILD_ID
        ? Routes.applicationGuildCommands(CLIENT_ID, GUILD_ID)
        : Routes.applicationCommands(CLIENT_ID);

    console.log(`Déploiement de ${commands.length} commandes ${GUILD_ID ? "(guilde)" : "(globales, propagation ~1h)"}...`);
    await rest.put(target, { body: commands });
    console.log("Commandes déployées avec succès.");
})().catch((error) => {
    console.error(error);
    process.exit(1);
});
