const {
    EmbedBuilder,
    ActionRowBuilder,
    ButtonBuilder,
    ButtonStyle,
    ModalBuilder,
    TextInputBuilder,
    TextInputStyle,
    MessageFlags,
} = require("discord.js");

const LOG_CHANNEL_ID = process.env.LOG_CHANNEL_ID;
const STAFF_ROLE_ID = process.env.STAFF_ROLE_ID;

const CATEGORIES = {
    probleme: "Problème",
    bug: "Bug",
    suggestion: "Suggestion",
    candidature: "Candidature Host",
};

const STATUS = {
    OPEN: { label: "Ouvert", color: 0xed4245 },
    TAKEN: { label: "Pris en charge", color: 0xfee75c },
    RESOLVED: { label: "Résolu", color: 0x57f287 },
    IGNORED: { label: "Ignoré", color: 0x99aab5 },
};

const ACTION_STATUS = {
    ticket_take: STATUS.TAKEN,
    ticket_resolve: STATUS.RESOLVED,
    ticket_ignore: STATUS.IGNORED,
};

function buildModal(commandName) {
    const category = CATEGORIES[commandName];
    return new ModalBuilder()
        .setCustomId(commandName)
        .setTitle(category)
        .addComponents(
            new ActionRowBuilder().addComponents(
                new TextInputBuilder()
                    .setCustomId("content")
                    .setLabel(`Décris ta ${category.toLowerCase()}`)
                    .setStyle(TextInputStyle.Paragraph)
                    .setRequired(true)
                    .setMaxLength(1000)
            )
        );
}

function buildActionRow(disabled) {
    return new ActionRowBuilder().addComponents(
        new ButtonBuilder().setCustomId("ticket_take").setLabel("Pris en charge").setStyle(ButtonStyle.Primary).setDisabled(disabled),
        new ButtonBuilder().setCustomId("ticket_resolve").setLabel("Résolu").setStyle(ButtonStyle.Success).setDisabled(disabled),
        new ButtonBuilder().setCustomId("ticket_ignore").setLabel("Ignoré").setStyle(ButtonStyle.Secondary).setDisabled(disabled)
    );
}

async function handleTicketCommand(interaction) {
    if (!interaction.isChatInputCommand()) return false;
    if (!CATEGORIES[interaction.commandName]) return false;

    await interaction.showModal(buildModal(interaction.commandName));
    return true;
}

async function handleTicketModalSubmit(interaction) {
    if (!interaction.isModalSubmit()) return false;
    const category = CATEGORIES[interaction.customId];
    if (!category) return false;

    const content = interaction.fields.getTextInputValue("content");

    if (LOG_CHANNEL_ID) {
        const logChannel = await interaction.client.channels.fetch(LOG_CHANNEL_ID).catch(() => null);
        if (logChannel) {
            const embed = new EmbedBuilder()
                .setColor(STATUS.OPEN.color)
                .setAuthor({ name: interaction.user.tag, iconURL: interaction.user.displayAvatarURL() })
                .setDescription(content)
                .addFields(
                    { name: "Catégorie", value: category, inline: true },
                    { name: "Statut", value: STATUS.OPEN.label, inline: true }
                )
                .setTimestamp();

            await logChannel.send({ embeds: [embed], components: [buildActionRow(false)] });
        } else {
            console.error("LOG_CHANNEL_ID introuvable ou inaccessible.");
        }
    }

    await interaction.reply({
        content: "Ton message a été transmis au staff en privé, merci !",
        flags: MessageFlags.Ephemeral,
    });
    return true;
}

async function handleTicketButton(interaction) {
    if (!interaction.isButton()) return false;
    const newStatus = ACTION_STATUS[interaction.customId];
    if (!newStatus) return false;

    if (STAFF_ROLE_ID && !interaction.member.roles.cache.has(STAFF_ROLE_ID)) {
        await interaction.reply({ content: "Cette action est réservée au staff.", flags: MessageFlags.Ephemeral });
        return true;
    }

    const oldEmbed = interaction.message.embeds[0];
    const embed = EmbedBuilder.from(oldEmbed)
        .setColor(newStatus.color)
        .setFields(
            oldEmbed.fields.map((field) =>
                field.name === "Statut"
                    ? { name: "Statut", value: `${newStatus.label} (par ${interaction.user.username})`, inline: true }
                    : field
            )
        );

    const closed = newStatus === STATUS.RESOLVED || newStatus === STATUS.IGNORED;
    await interaction.update({ embeds: [embed], components: [buildActionRow(closed)] });
    return true;
}

module.exports = { CATEGORIES, handleTicketCommand, handleTicketModalSubmit, handleTicketButton };
