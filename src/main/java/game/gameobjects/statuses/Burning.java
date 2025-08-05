package game.gameobjects.statuses;

import static game.App.randomNumber;

import java.util.HashSet;
import java.util.List;
import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.LightSource;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.terrains.Fire;

public class Burning extends Status implements Behavable, LightSource, FiltersIn{
    
    private int minDamage;
    private int maxDamage;
    private int turns;

    private int minIntensity;
    private int maxIntensity;
    
    public Burning(){
        this(1, 3, 3, 0, 1);
    }

    public Burning(int minDamage, int maxDamage, int turns, int minIntensity, int maxIntensity) {
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.turns = turns;
        this.minIntensity = minIntensity;
        setCharacter('▒');
        setfGColor(TileColor.create(252, 132, 3, 255));
        setModifiers(new HashSet<Modifier>(List.of(Modifiers.blink())));
        setDescriptor("Burning");
        setTileName("Fire");
    }

    @Override
    public int behave() {
        owner.dealDamage(randomNumber(minDamage, maxDamage), DamageType.FIRE);
        if (Fire.isFlammable(owner.getSpace())) {
            owner.getSpace().addFire(new Fire(1));
        }
        for (Space space : Space.getAdjacentSpaces(owner.getSpace())) {
            if (Fire.isFlammable(space)) {
                space.addFire(new Fire(1));
            }
            if (space.isOccupied()) {
                space.getOccupant().addStatus(new Burning());
            }
        }
        turns--;
        minDamage += minIntensity;
        maxDamage += maxIntensity;
        if (turns <= 0){
            owner.removeStatus(this);
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }


    public int getTurns() {
        return turns;
    }
    
    public void subtractTurns(int amount){
        turns -= amount;
        if (turns < 0){
            turns = 0;
        }
    }
    public void addTurns(int amount){
        turns += amount;
    }

    @Override
    public int getLightSourceIntensity() {
        return (int)(turns * 2.5);
    }

    @Override
    public boolean filterIn(Status status) {
        if (status instanceof Burning burning) {
            burning.turns++;
            return true;
        }
        return false;
    }
    
}
