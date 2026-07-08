const { EmbedBuilder, ActionRowBuilder, ButtonBuilder, ButtonStyle, MessageFlags } = require("discord.js");

const TICKET_CHANNEL_IDS = (process.env.TICKET_CHANNEL_IDS || "")
    .split(",")
    .map((id) => id.trim())
    .filter(Boolean);
const LOG_CHANNEL_ID = process.env.LOG_CHANNEL_ID;
const STAFF_ROLE_ID = process.env.STAFF_ROLE_ID;

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

function buildTicketEmbed(message, status, handledBy) {
    return new EmbedBuilder()
        .setColor(status.color)
        .setAuthor({ name: message.author.tag, iconURL: message.author.displayAvatarURL() })
        .addFields(
            { name: "Salon", value: `<#${message.channelId}>`, inline: true },
            { name: "Statut", value: handledBy ? `${status.label} (par ${handledBy})` : status.label, inline: true }
        )
        .setFooter({ text: `Ticket #${message.id}` })
        .setTimestamp(message.createdAt);
}

function buildActionRow(linkUrl, disabled) {
    return new ActionRowBuilder().addComponents(
        new ButtonBuilder().setCustomId("ticket_take").setLabel("Pris en charge").setStyle(ButtonStyle.Primary).setDisabled(disabled),
        new ButtonBuilder().setCustomId("ticket_resolve").setLabel("Résolu").setStyle(ButtonStyle.Success).setDisabled(disabled),
        new ButtonBuilder().setCustomId("ticket_ignore").setLabel("Ignoré").setStyle(ButtonStyle.Secondary).setDisabled(disabled),
        new ButtonBuilder().setLabel("Aller au message").setStyle(ButtonStyle.Link).setURL(linkUrl)
    );
}

async function handleTicketMessage(message) {
    if (message.author.bot) return;
    if (!TICKET_CHANNEL_IDS.includes(message.channelId)) return;
    if (!LOG_CHANNEL_ID) return;

    const logChannel = await message.client.channels.fetch(LOG_CHANNEL_ID).catch(() => null);
    if (!logChannel) {
        console.error("LOG_CHANNEL_ID introuvable ou inaccessible.");
        return;
    }

    const messageUrl = `https://discord.com/channels/${message.guildId}/${message.channelId}/${message.id}`;
    const embed = buildTicketEmbed(message, STATUS.OPEN);
    const row = buildActionRow(messageUrl, false);

    await logChannel.send({ embeds: [embed], components: [row] });
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

    const linkButton = interaction.message.components[0].components.find((c) => c.style === ButtonStyle.Link);
    const closed = newStatus === STATUS.RESOLVED || newStatus === STATUS.IGNORED;
    const row = buildActionRow(linkButton.url, closed);

    await interaction.update({ embeds: [embed], components: [row] });
    return true;
}

module.exports = { handleTicketMessage, handleTicketButton };
