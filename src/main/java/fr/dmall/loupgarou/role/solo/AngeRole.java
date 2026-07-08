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
        lines.add("L'Ange est un rôle solitaire qui peut prendre deux formes distinctes.");

        if (form == Form.UNDECIDED) {
            lines.add("§eDéterminez votre forme via /lg ange dechu ou /lg ange gardien :");
            lines.add(" - Ange Déchu : débute à 12 cœurs, passe à 15 en provoquant (ou en contribuant à) la mort de sa cible. Victoire en solitaire.");
            lines.add(" - Ange Gardien : débute à 15 cœurs, sa priorité est la victoire de son protégé. La mort de ce dernier le fait retomber à 12 cœurs avec une Faiblesse permanente, et il doit alors gagner seul.");
            return lines.toArray(new String[0]);
        }

        String linkedName = (linkedPlayer != null) ? Bukkit.getOfflinePlayer(linkedPlayer).getName() : "Inconnu";

        if (form == Form.DECHU) {

            lines.add("§cForme Déchue : la victoire ne peut venir que de vous seul.");
            lines.add("Cible assignée : " + linkedName + (conditionFulfilled ? " §a(éliminée — vous êtes passé à 15 cœurs)" : ""));

        } else {

            lines.add("§bForme Gardienne : la victoire de " + linkedName + " est votre priorité absolue.");

            if (protegeDead) {
                lines.add("§cVotre protégé n'a pas survécu. Vous êtes retombé à 12 cœurs, affligé d'une Faiblesse permanente, et devez désormais viser la victoire seul.");
            } else {
                lines.add("Dès que votre protégé descend sous 4 cœurs, /lg regen lui accorde Régénération I pendant 1 minute (utilisable une seule fois par partie).");
            }

        }

        return lines.toArray(new String[0]);

    }

}
