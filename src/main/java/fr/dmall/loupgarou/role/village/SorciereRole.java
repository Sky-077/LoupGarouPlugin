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
                "Potion de vie : quand un joueur non infecté est sur le point de mourir, vous recevez un message cliquable (10s) pour le sauver avec /lg soigner <joueur> (1 fois par partie).",
                "Potion de mort : retirez 2 cœurs de vie maximum, définitivement, à un joueur avec /lg empoisonner <joueur> (1 fois par partie).",
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
