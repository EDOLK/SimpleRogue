package game.gameobjects.statuses;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.HasStatusVulns;
import game.gamelogic.OverridesMovement;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.MovementResult;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public class Rooted extends Status implements OverridesMovement, Behavable, FiltersOut, HasStatusVulns {

    private int timer = 5;

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
    public MovementResult overrideMovement(MovementResult result, Entity entity, Space toSpace) {
        return result.withSuccessful(false);
    }

    @Override
    public boolean isVulnerable(Status status) {
        return status instanceof Burning;
    }

    @Override
    public boolean filterOut(Status status) {
        switch (status) {
            case Burning burning -> {
                this.owner.removeStatus(this);
                return false;
            }
            case Rooted rooted -> {
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
