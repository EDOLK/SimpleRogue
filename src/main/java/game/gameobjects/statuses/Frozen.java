package game.gameobjects.statuses;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.OverridesBehavable;
import game.gamelogic.behavior.Behavable;

public class Frozen extends Status implements SeperateIn, Behavable, OverridesBehavable {

    private int turns = 5;

    public Frozen() {
        super();
        this.setCharacter(' ');
        setfGColor(TileColor.transparent());
        setbGColor(TileColor.create(100, 100, 255, 100));
        setDescriptor("Frozen");
        setTileName("Freezing Air");
    }

    @Override
    public int overrideBehave(Behavable behavable) {
        return 100;
    }

    @Override
    public boolean overrideIsActive(Behavable behavable) {
        return this.owner != null && this.owner.isAlive();
    }

    @Override
    public int behave() {
        turns--;
        if (turns <= 0) {
            this.owner.removeStatus(this);
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return this.owner != null && this.owner.isAlive();
    }

    @Override
    public boolean onStackIn(Status sameStatus) {
        return true;
    }

    @Override
    public boolean validateSamenessIn(Status status) {
        return status instanceof Freezing || status instanceof Frozen;
    }

}
