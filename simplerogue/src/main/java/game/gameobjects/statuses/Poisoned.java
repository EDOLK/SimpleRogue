package game.gameobjects.statuses;

import static game.App.randomNumber;

import java.util.HashSet;
import java.util.List;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.behavior.Behavable;
import game.gameobjects.DamageType;

public class Poisoned extends Status implements Behavable, Seperate{

    private int minDamage;
    private int maxDamage;
    private int turns;

    public Poisoned(int minDamage, int maxDamage, int turns) {
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.turns = turns;
        setCharacter(' ');
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(50, 200, 50, 100));
        HashSet<Modifier> modifiers = new HashSet<Modifier>();
        modifiers.add(Modifiers.blink());
        setModifiers(modifiers);
        setDescriptor("Poisoned");
    }

    @Override
    public int behave() {
        if (owner.getHP() > 0){
            owner.dealDamage(randomNumber(minDamage, maxDamage), DamageType.POISON);
        }
        turns--;
        if (turns <= 0){
            owner.removeStatus(this);
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }

    @Override
    public void onStack(Status SameStatus) {
        
    }

    @Override
    public Status validateSameness(List<Status> Statuses) {
        for (Status status : Statuses) {
            if (status instanceof Poisoned poisoned){
                return poisoned;
            }
        }
        return null;
    }
    
}
