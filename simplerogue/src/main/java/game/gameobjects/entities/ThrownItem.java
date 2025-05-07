package game.gameobjects.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class ThrownItem extends Entity implements HasDodge, HasResistances, Behavable{

    private Iterator<Space> pathToTarget;
    private Aimable aimable;
    private int speed = 1;
    private ArrayList<Resistance> resistances = new ArrayList<Resistance>();
    
    public ThrownItem(Aimable aimable, Space spawnSpace, Space targetSpace, int speed){
        this.aimable = aimable;
        setSpace(spawnSpace);
        setMaxHP(1);
        setHP(1);
        List<Space> l = Line.getLineAsListInclusive(getSpace(), targetSpace);
        l.remove(0);
        pathToTarget = l.iterator();
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
    public int behave() {
        if (pathToTarget.hasNext()){
            Space nextSpace = pathToTarget.next();
            if(!Space.moveEntity(this, nextSpace) && nextSpace.isOccupied()){
                aimable.onHit(nextSpace.getOccupant());
                if (aimable.landsOnHit())
                    aimable.onLand(nextSpace);
                kill(null);
            }
        } else {
            aimable.onLand(getSpace());
            kill(null);
        }
        return this.getTimeToMove();
    }

    @Override
    public int getBaseMoveTime() {
        return 100/speed;
    }

    @Override
    public int defaultInteraction(Entity entity) {
        return entity.getTimeToWait();
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
    public Tile getTile(double percent) {
        if (aimable instanceof DisplayableTile displayableAimable){
            return displayableAimable.getTile(percent);
        }
        return super.getTile(percent);
    }
    
}
