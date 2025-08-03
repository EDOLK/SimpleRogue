package game.gameobjects.statuses;

import java.util.HashSet;
import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.HasDodge;
import game.gamelogic.behavior.Behavable;

public class Flying extends Status implements Behavable, HasDodge, SeperateIn{

    private int turns;
    private boolean perminent;

    public int getTurns() {
        return turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public boolean isPermanent() {
        return perminent;
    }

    public void setPerminent(boolean perminent) {
        this.perminent = perminent;
    }

    public Flying() {
        setCharacter('^');
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(200, 200, 200, 100));
        HashSet<Modifier> modifiers = new HashSet<Modifier>();
        modifiers.add(Modifiers.blink());
        setModifiers(modifiers);
        setDescriptor("Flying");
    }

    @Override
    public int getDodge() {
        return 10;
    }

    @Override
    public int behave() {
        turns--;
        if (turns <= 0 && !perminent){
            owner.removeStatus(this);
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }

    @Override
    public boolean filterIn(Status status) {
        if (status instanceof Flying otherFlying) {
            if (!this.isPermanent() && !otherFlying.isPermanent()){
                otherFlying.setTurns(otherFlying.getTurns()+this.getTurns());
            }
            return true;
        }
        return false;
    }
    
}
