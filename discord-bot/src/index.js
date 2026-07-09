require("dotenv").config();
const { Client, GatewayIntentBits, EmbedBuilder, MessageFlags } = require("discord.js");
const roles = require("./roles");
const { startKeepAliveServer } = require("./keepalive");
const { handleTicketCommand, handleTicketModalSubmit, handleTicketButton, handleChannelMessage } = require("./tickets");

startKeepAliveServer();

const TEAM_COLORS = {
    VILLAGE: 0x57f287,
    LOUP: 0xed4245,
    NEUTRAL: 0x99aab5,
};

const TEAM_LABELS = {
    VILLAGE: "Village",
    LOUP: "Loups",
    NEUTRAL: "Solitaire",
};

const rolesByCommand = new Map(roles.map((role) => [role.command, role]));

const client = new Client({
    intents: [GatewayIntentBits.Guilds, GatewayIntentBits.GuildMessages],
});

client.once("ready", () => {
    console.log(`Connecté en tant que ${client.user.tag}`);
});

client.on("messageCreate", (message) => {
    handleChannelMessage(message).catch((error) => console.error("Erreur suppression message :", error.message));
});

client.on("interactionCreate", async (interaction) => {
    if (await handleTicketButton(interaction)) return;
    if (await handleTicketCommand(interaction)) return;
    if (await handleTicketModalSubmit(interaction)) return;
    if (!interaction.isChatInputCommand()) return;

    const role = rolesByCommand.get(interaction.commandName);
    if (!role) return;

    const embed = new EmbedBuilder()
        .setTitle(role.name)
        .setColor(TEAM_COLORS[role.team])
        .addFields({ name: "Camp", value: TEAM_LABELS[role.team] })
        .setDescription(role.lines.join("\n\n"));

    try {
        await interaction.reply({ embeds: [embed], flags: MessageFlags.Ephemeral });
    } catch (error) {
        console.error(`Échec de réponse pour /${interaction.commandName} :`, error.message);
    }
});

client.login(process.env.DISCORD_TOKEN);
