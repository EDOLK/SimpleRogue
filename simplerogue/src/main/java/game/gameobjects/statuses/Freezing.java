package game.gameobjects.statuses;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.behavior.Behavable;

public class Freezing extends Status implements Seperate, Behavable{
    
    private int timer = 10;

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
    public String getDescriptor() {
        return super.getDescriptor() + "(" + timer + ") ";
    }

    @Override
    public void onStack(Status sameStatus) {
        if (sameStatus instanceof Freezing freezing) {
            freezing.timer+=5;
        }
    }

    @Override
    public Status validateSameness(List<Status> statuses) {
        for (Status status : statuses) {
            if (status instanceof Freezing freezing){
                return freezing;
            }
            if (status instanceof Frozen frozen) {
                return frozen;
            }
        }
        return null;
    }

    @Override
    public void behave() {
        if (timer <= 0) {
            owner.removeStatus(this);
            return;
        }
        timer--;
    }

    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }

}
