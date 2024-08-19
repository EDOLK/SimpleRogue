package game.gameobjects.entities;

import java.util.ArrayList;
import java.util.Iterator;

import org.hexworks.zircon.api.data.Tile;

import game.Line;
import game.gamelogic.Aimable;
import game.gamelogic.Examinable;
import game.gamelogic.HasDodge;
import game.gamelogic.HasResistances;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.DisplayableTile;
import game.gameobjects.Space;

public class ThrownItem extends Entity implements Behavable, HasDodge, HasResistances{

    private Iterator<Space> pathToTarget;
    private Aimable aimable;
    private int speed = 1;
    private ArrayList<Resistance> resistances = new ArrayList<Resistance>();
    
    public ThrownItem(Aimable aimable, Space spawnSpace, Space targetSpace, int speed){
        this.aimable = aimable;
        setSpace(spawnSpace);
        setMaxHP(1);
        setHP(1);
        pathToTarget = Line.getLineAsArrayListIncludeEnd(getSpace(), targetSpace).iterator();
        setCorpse(null);
        if (aimable instanceof Examinable examinable){
            setName("Thrown " + examinable.getName());
            setDescription(examinable.getDescription());
        }
        this.speed = speed;
        for (DamageType damageType : DamageType.values()) {
            resistances.add(new PercentageResistance(damageType, 1.0));
        }
    }

    public Iterator<Space> getPathToTarget() {
        return pathToTarget;
    }

    @Override
    public void behave() {
        for (int i = 0; i < speed; i++) {
            if (pathToTarget.hasNext()){
                Space nextSpace = pathToTarget.next();
                if(!Space.moveEntity(this, nextSpace)){
                    aimable.onHit(nextSpace.getOccupant());
                    if (aimable.landsOnHit())
                        aimable.onLand(nextSpace);
                    onKill(null);
                    break;
                }
            } else {
                aimable.onLand(getSpace());
                onKill(null);
                break;
            }
        }
    }

    @Override
    public void defaultInteraction(Entity entity) {

    }

    @Override
    public int getDodge() {
        return 99;
    }

    @Override
    public ArrayList<Resistance> getResistances() {
        return resistances;
    }

    @Override
    public boolean isActive() {
        return isAlive();
    }

    @Override
    public void onKill(Entity killer) {
        Space space = getSpace();
        space.setOccupant(null);
        setSpace(null);
    }

    @Override
    public Tile getTile(double percent) {
        if (aimable instanceof DisplayableTile displayableAimable){
            return displayableAimable.getTile(percent);
        }
        return super.getTile(percent);
    }
    
}
