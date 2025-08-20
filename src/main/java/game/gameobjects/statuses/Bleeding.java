package game.gameobjects.statuses;

import static game.App.randomNumber;

import java.util.HashSet;
import java.util.List;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.behavior.Behavable;
import game.gameobjects.DamageType;

public class Bleeding extends Status implements Behavable, FiltersOut{

    private int remainingDamage;
    private int minDamage;
    private int maxDamage;

    public Bleeding(int damage, int minDamage, int maxDamage){
        this.remainingDamage = damage;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        setCharacter(' ');
        setBgColor(TileColor.transparent());
        setFgColor(TileColor.create(200, 50, 50, 100));
        setModifiers(new HashSet<Modifier>(List.of(Modifiers.blink())));
        setDescriptor("Bleeding");
    }

    @Override
    public int behave() {

        int damageToBeDelt = randomNumber(Math.min(minDamage,remainingDamage), Math.min(maxDamage,remainingDamage));

        if (owner.isAlive())
            owner.dealDamage(damageToBeDelt, DamageType.BLEED);

        remainingDamage -= damageToBeDelt;

        if (remainingDamage <= 0)
            owner.removeStatus(this);

        return 100;
    }

    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }

    @Override
    public boolean filterOut(Status status) {
        if (status instanceof Bleeding bleeding) {
            this.minDamage += bleeding.minDamage;
            this.maxDamage += bleeding.maxDamage;
            this.remainingDamage += bleeding.remainingDamage;
            return true;
        }
        return false;
    }
    
}
