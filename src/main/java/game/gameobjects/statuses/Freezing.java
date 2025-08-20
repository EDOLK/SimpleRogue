package game.gameobjects.statuses;

import java.util.Set;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.time.ModifiesAttackTime;
import game.gamelogic.time.ModifiesMoveTime;

public class Freezing extends Status implements FiltersIn, FiltersOut, Behavable, ModifiesMoveTime, ModifiesAttackTime{
    
    private int turns = 10;
    private int freezeLimit = 20;

    public Freezing(){
        super();
        setCharacter('*');
        setModifiers(Set.of(Modifiers.blink()));
        setFgColor(TileColor.create(100, 100, 255, 255));
        setBgColor(TileColor.transparent());
        setDescriptor("Freezing");
        setTileName("Freezing Air");
    }

    @Override
    public int behave() {
        turns--;
        if (turns >= freezeLimit) {
            this.owner.addStatus(new Frozen());
        } else if (turns <= 0) {
            this.owner.removeStatus(this);
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return this.owner != null && this.owner.isAlive();
    }

    @Override
    public int modifyAttackTime(int time) {
        return time + (int)App.lerp(0, 0, freezeLimit, time, turns);
    }

    @Override
    public int modifyMoveTime(int time) {
        return time + (int)App.lerp(0, 0, freezeLimit, time, turns);
    }

    @Override
    public boolean filterIn(Status status) {
        switch (status) {
            case Burning burning -> {
                burning.getOwner().removeStatus(burning);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public boolean filterOut(Status status) {
        switch (status) {
            case Freezing freezing -> {
                this.turns += freezing.turns;
                return true;
            }
            case Burning burning -> {
                this.owner.removeStatus(this);
                return true;
            }
            default -> {
                return false;
            }
        }
    }
    
}
