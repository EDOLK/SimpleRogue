package game.gameobjects.statuses;

import java.util.HashSet;
import java.util.Set;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.App;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.time.ModifiesAttackTime;
import game.gamelogic.time.ModifiesMoveTime;
import game.gameobjects.entities.Entity;

public class Freezing extends Status implements SeperateIn, Behavable, ModifiesMoveTime, ModifiesAttackTime{
    
    private int turns = 10;
    private int freezeLimit = 20;

    public Freezing(){
        super();
        this.setCharacter('*');
        Set<Modifier> modSet = new HashSet<Modifier>();
        modSet.add(Modifiers.blink());
        setModifiers(modSet);
        setfGColor(TileColor.create(100, 100, 255, 255));
        setbGColor(TileColor.transparent());
        setDescriptor("Freezing");
        setTileName("Freezing Air");
    }

    @Override
    public int behave() {
        turns--;
        if (turns >= freezeLimit) {
            Entity owner = this.owner;
            owner.removeStatus(this);
            owner.addStatus(new Frozen());
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
        if (status instanceof Freezing freezing) {
            freezing.turns += this.turns;
            return true;
        }
        if (status instanceof Frozen) {
            return true;
        }
        return false;
    }
    
}
