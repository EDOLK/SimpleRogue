package game.gameobjects.statuses;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.HasStatusVulns;
import game.gamelogic.OverridesMovement;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public class Rooted extends Status implements OverridesMovement, Behavable, SeperateOut, HasStatusVulns {
    private int timer = 10;

    public Rooted() {
        super();
        setCharacter('=');
        setfGColor(TileColor.create(87, 35, 3, 255));
        setbGColor(TileColor.transparent());
        setDescriptor("Rooted");
    }

    @Override
    public int behave() {
        if (timer > 0) {
            timer--;
        } else {
            this.owner.removeStatus(this);
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return this.owner != null && this.owner.isAlive();
    }

    @Override
    public boolean overrideMovement(Entity entity, Space toSpace) {
        return false;
    }

    @Override
    public boolean isVulnerable(Status status) {
        return status instanceof Burning;
    }

    @Override
    public boolean onStackOut(Status sameStatus) {
        switch (sameStatus) {
            case Burning burning -> {
                this.owner.removeStatus(this);
                return false;
            }
            case Rooted rooted -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public boolean validateSamenessOut(Status status) {
        return status instanceof Burning || status instanceof Rooted;
    }
}
