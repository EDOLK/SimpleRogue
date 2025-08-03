package game.gameobjects.statuses;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.HasStatusVulns;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.terrains.Moss;

public class Mossy extends Status implements SeperateOut, HasStatusVulns, Behavable{

    public Mossy() {
        super();
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(40,120,40,255));
        setCharacter('░');
        setDescriptor("Mossy");
    }

    @Override
    public boolean isVulnerable(Status status) {
        return status instanceof Burning;
    }

    @Override
    public int behave() {
        Moss.trySpread(this.owner.getSpace(), true, true);
        // Space.getAdjacentSpaces(this.owner.getSpace()).forEach((s) -> Moss.trySpread(s, true, true));
        return 1000;
    }

    @Override
    public boolean isActive() {
        return Status.isActiveHelper(this);
    }

    @Override
    public boolean filterOut(Status status) {
        switch (status) {
            case Burning burning -> {
                this.owner.removeStatus(this);
                return false;
            }
            case Mossy mossy -> {
                return true;
            }
            default -> {
            }
        }
        return false;
    }
    
}
