package game.gameobjects.statuses;

import static game.App.randomNumber;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.Dungeon;
import game.gamelogic.LightSource;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.terrains.Fire;

public class Burning extends Status implements Behavable, LightSource, Seperate{
    
    private int minDamage;
    private int maxDamage;
    private int turns;

    private int minIntensity;
    private int maxIntensity;
    
    
    public Burning(){
        this(1, 3, 5, 0, 1);
    }

    public Burning(int minDamage, int maxDamage, int turns, int minIntensity, int maxIntensity) {
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.turns = turns;
        this.minIntensity = minIntensity;
        Set<Modifier> mods = new HashSet<Modifier>();
        mods.add(Modifiers.blink());
        setCharacter('▒');
        setfGColor(TileColor.create(252, 132, 3, 255));
        setModifiers(mods);
        setDescriptor("Burning");
        setTileName("Fire");
    }

    @Override
    public void behave() {
        Space currentSpace = null;
        currentSpace = owner.getSpace();
        if (owner.getHP() > 0){
            owner.dealDamage(randomNumber(minDamage, maxDamage), DamageType.FIRE);
        }
        if (currentSpace != null){
            int x = currentSpace.getX();
            int y = currentSpace.getY();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int toX = x + i;
                    int toY = y + j;
                    try {
                        Space toSpace = Dungeon.getCurrentFloor().getSpace(toX, toY);
                        if (Fire.isFlammable(toSpace)){
                            toSpace.addFire(new Fire(1));
                        }
                        if (i == 0 && j == 0)
                            continue;
                        if (toSpace.isOccupied()){
                            toSpace.getOccupant().addStatus(new Burning());
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }
        turns--;
        minDamage += minIntensity;
        maxDamage += maxIntensity;
        if (turns <= 0){
            owner.removeStatus(this);
        }
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
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }

    @Override
    public int getLightSourceIntensity() {
        return 5;
    }

    @Override
    public void onStack(Status SameStatus) {

    }

    @Override
    public Status validateSameness(List<Status> Statuses) {
        for (Status status : Statuses) {
            if (status instanceof Burning burning){
                return burning;
            }
        }
        return null;
    }
    
}
