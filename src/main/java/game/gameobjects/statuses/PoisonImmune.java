package game.gameobjects.statuses;

import org.hexworks.zircon.api.data.Tile;

import game.display.Display;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;

public class PoisonImmune extends Status implements FiltersIn, FiltersOut, Behavable {

    private int turns;

    public PoisonImmune(int turns) {
        this.turns = turns;
    }

    @Override
    public boolean filterOut(Status status) {
        if (status instanceof Poisoned) {
            return true;
        }
        return false;
    }

    @Override
    public boolean filterIn(Status status) {
        if (status instanceof Poisoned poison) {
            Entity owner = poison.getOwner();
            if (owner instanceof PlayerEntity) {
                Display.log("You feel better.");
            }
            poison.getOwner().removeStatus(poison);
        }
        if (status instanceof PoisonImmune pi) {
            pi.turns += this.turns;
            return true;
        }
        return false;
    }

    @Override
    public int behave() {
        turns--;
        if (turns <= 0)
            this.getOwner().removeStatus(this);
        return 100;
    }

    @Override
    public boolean isActive() {
        return Status.isActiveHelper(this);
    }

    @Override
    public Tile getTile(double percent) {
        return Tile.empty();
    }
    
}
