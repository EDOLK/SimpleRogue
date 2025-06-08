package game.gameobjects.statuses;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.behavior.Behavable;

public class BeginningHunt extends Status implements Behavable {
    public BeginningHunt() {
        super();
        this.setCharacter('!');
        this.setbGColor(TileColor.create(0,0,0,0));
        this.setfGColor(TileColor.create(255,25,25,255));
        this.setyOffset(-1);
        this.setFullBright(true);
    }

    @Override
    public int behave() {
        this.owner.removeStatus(this);
        return 1;
    }

    @Override
    public boolean isActive() {
        return this.owner != null && this.owner.isAlive();
    }
}
