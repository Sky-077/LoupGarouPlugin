package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

public class SorciereRole extends Role {

    private boolean healAvailable = true;
    private boolean poisonAvailable = true;

    public SorciereRole() {
        super("Sorcière", RoleTeam.VILLAGE);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Potion de vie : la mort imminente d'un joueur non infecté vous envoie un message cliquable (valable 10s) permettant de le sauver via /lg soigner <joueur> (une fois par partie).",
                "Potion de mort : /lg empoisonner <joueur> retire 2 cœurs de vie maximum à une cible, de façon irréversible (une fois par partie).",
        };
    }

    public boolean isHealAvailable() {
        return healAvailable;
    }

    public void consumeHeal() {
        healAvailable = false;
    }

    public boolean isPoisonAvailable() {
        return poisonAvailable;
    }

    public void consumePoison() {
        poisonAvailable = false;
    }

}
