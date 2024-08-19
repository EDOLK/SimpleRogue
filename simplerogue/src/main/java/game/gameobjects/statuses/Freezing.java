package game.gameobjects.statuses;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.OverridesMovement;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public class Freezing extends Status implements Seperate, Behavable, OverridesMovement{
    
    private int turns = 0;
    private int limit = 1;
    private boolean enabled = false;

    public Freezing(){
        super();
        this.setCharacter('*');
        Set<Modifier> modSet = new HashSet<Modifier>();
        modSet.add(Modifiers.blink());
        setModifiers(modSet);
        this.setfGColor(TileColor.create(100, 100, 255, 255));
        this.setbGColor(TileColor.transparent());
        this.setDescriptor("Freezing");
    }

    @Override
    public void onStack(Status SameStatus) {

    }

    @Override
    public Status validateSameness(List<Status> Statuses) {
        for (Status status : Statuses) {
            if (status instanceof Freezing freezing){
                return freezing;
            }
        }
        return null;
    }

    @Override
    public boolean overrideMovement(Entity entity, Space toSpace) {
        return false;
    }

    @Override
    public void behave() {
        if (turns >= limit){
            turns = 0;
            enabled = !enabled;
        } else {
            turns ++;
        }
    }

    @Override
    public boolean isActive() {
        return owner.isAlive();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
}
