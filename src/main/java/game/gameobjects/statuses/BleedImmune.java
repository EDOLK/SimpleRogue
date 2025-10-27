package game.gameobjects.statuses;

import org.hexworks.zircon.api.data.Tile;

import game.display.Display;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;

public class BleedImmune extends Status implements FiltersIn, FiltersOut, Behavable {

    private int turns;

    public BleedImmune(int turns) {
        this.turns = turns;
    }

    @Override
    public boolean filterOut(Status status) {
        if (status instanceof Bleeding) {
            return true;
        }
        return false;
    }

    @Override
    public boolean filterIn(Status status) {
        if (status instanceof Bleeding bleed) {
            Entity owner = bleed.getOwner();
            if (owner instanceof PlayerEntity) {
                Display.log("The Bandage staves some of your bleeding.");
            }
            bleed.getOwner().removeStatus(bleed);
        }
        if (status instanceof BleedImmune bi) {
            bi.turns += this.turns;
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
