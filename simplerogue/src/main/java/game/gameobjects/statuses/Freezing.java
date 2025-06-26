package game.gameobjects.statuses;

import java.util.HashSet;
import java.util.Set;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.OverridesMovement;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public class Freezing extends Status implements SeperateIn, Behavable, OverridesMovement{
    
    private int turns = 0;
    private int limit = 1;
    private boolean enabled = false;

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
    public boolean onStackIn(Status sameStatus) {
        if (sameStatus instanceof Freezing) {
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideMovement(Entity entity, Space toSpace) {
        return false;
    }

    @Override
    public int behave() {
        if (turns >= limit){
            turns = 0;
            enabled = !enabled;
        } else {
            turns ++;
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return owner.isAlive();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean validateSamenessIn(Status status) {
        return status instanceof Freezing;
    }
    
}
