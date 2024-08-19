package game.gameobjects.statuses;

import static game.App.randomNumber;

import java.util.HashSet;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.behavior.Behavable;

public class Healing extends Status implements Behavable{

    private int healAmount;
    private int minHeal;
    private int maxHeal;

    public Healing(int healAmount, int minHeal, int maxHeal) {
        this.healAmount = healAmount;
        this.minHeal = minHeal;
        this.maxHeal = maxHeal;
        setCharacter(' ');
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(255, 10, 10, 100));
        HashSet<Modifier> modifiers = new HashSet<Modifier>();
        modifiers.add(Modifiers.blink());
        setModifiers(modifiers);
        setDescriptor("Healing");
    }

    @Override
    public void behave() {
        int r = randomNumber(minHeal, maxHeal);
        owner.heal(r);
        healAmount -= r;
        if (healAmount <= 0){
            owner.removeStatus(this);
        }
    }
    
    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }
}
