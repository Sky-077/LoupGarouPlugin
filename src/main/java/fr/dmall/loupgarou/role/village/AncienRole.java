package fr.dmall.loupgarou.role.village;

import fr.dmall.loupgarou.role.Role;
import fr.dmall.loupgarou.role.RoleTeam;

public class AncienRole extends Role {

    private boolean resistanceAvailable = true;
    private boolean reviveAvailable = true;

    public AncienRole() {
        super("Ancien", RoleTeam.VILLAGE);
    }

    @Override
    public String[] getInstructions() {
        return new String[] {
                "Votre sort est lié à celui du Village.",
                "Une Résistance 0.5 permanente réduit de 10% tous les dégâts que vous subissez.",
                "Un Loup-Garou ou un Père des Loups qui vous achève vous voit ressusciter (une fois par partie) avec votre vie du moment — mais votre Résistance 0.5 disparaît alors pour de bon.",
                "Un villageois (hors Loups et solitaires) qui vous tue vous laisse mourir normalement, mais perd pour toujours la moitié de sa vie maximale en retour.",
                "Contre un solitaire, votre mort se déroule sans aucun effet secondaire.",
        };
    }

    public boolean isResistanceAvailable() {
        return resistanceAvailable;
    }

    public void loseResistance() {
        resistanceAvailable = false;
    }

    public boolean isReviveAvailable() {
        return reviveAvailable;
    }

    public void consumeRevive() {
        reviveAvailable = false;
    }

}
