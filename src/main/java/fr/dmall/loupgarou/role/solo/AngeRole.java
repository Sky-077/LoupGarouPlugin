package fr.dmall.loupgarou.role.solo;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AngeRole extends Role {

    public enum Form {
        UNDECIDED, DECHU, GARDIEN
    }

    private Form form = Form.UNDECIDED;
    private UUID linkedPlayer;
    private boolean conditionFulfilled;
    private boolean protegeDead;
    private boolean regenAvailable = true;

    public AngeRole() {
        super("Ange", RoleTeam.NEUTRAL);
    }

    public Form getForm() {
        return form;
    }

    public void chooseForm(Form form) {
        this.form = form;
    }

    public void setLinkedPlayer(UUID uuid) {
        this.linkedPlayer = uuid;
    }

    public UUID getLinkedPlayer() {
        return linkedPlayer;
    }

    public boolean isTarget(UUID uuid) {
        return form == Form.DECHU && uuid.equals(linkedPlayer);
    }

    public boolean isProtege(UUID uuid) {
        return form == Form.GARDIEN && uuid.equals(linkedPlayer);
    }

    public boolean isConditionFulfilled() {
        return conditionFulfilled;
    }

    public void fulfillCondition() {
        conditionFulfilled = true;
    }

    public boolean isProtegeDead() {
        return protegeDead;
    }

    public void markProtegeDead() {
        protegeDead = true;
    }

    public boolean isRegenAvailable() {
        return regenAvailable;
    }

    public void consumeRegen() {
        regenAvailable = false;
    }

    @Override
    public String[] getInstructions() {

        List<String> lines = new ArrayList<>();
        lines.add("Vous êtes l'Ange, un rôle solitaire à deux visages.");

        if (form == Form.UNDECIDED) {
            lines.add("§eChoisissez votre forme avec /lg ange dechu ou /lg ange gardien :");
            lines.add(" - Ange Déchu : 12 cœurs, doit tuer une cible (ou contribuer à sa mort) pour passer à 15 cœurs. Gagne seul.");
            lines.add(" - Ange Gardien : 15 cœurs, doit faire gagner son protégé en priorité. Si le protégé meurt, passe à 12 cœurs + Faiblesse permanente et doit gagner seul.");
            return lines.toArray(new String[0]);
        }

        String linkedName = (linkedPlayer != null) ? Bukkit.getOfflinePlayer(linkedPlayer).getName() : "Inconnu";

        if (form == Form.DECHU) {

            lines.add("§cAnge Déchu : vous devez gagner seul.");
            lines.add("Votre cible : " + linkedName + (conditionFulfilled ? " §a(éliminée, vous êtes à 15 cœurs)" : ""));

        } else {

            lines.add("§bAnge Gardien : vous devez faire gagner " + linkedName + " en priorité.");

            if (protegeDead) {
                lines.add("§cVotre protégé est mort. Vous êtes à 12 cœurs avec Faiblesse permanente, et devez désormais gagner seul.");
            } else {
                lines.add("Si votre protégé passe sous 4 cœurs, utilisez /lg regen pour lui donner Régénération I (1 minute, 1x/partie).");
            }

        }

        return lines.toArray(new String[0]);

    }

}
