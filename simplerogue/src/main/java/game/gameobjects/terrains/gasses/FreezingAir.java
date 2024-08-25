package game.gameobjects.terrains.gasses;

import java.util.List;
import java.util.Stack;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Freezing;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.liquids.Liquid;

public class FreezingAir extends Gas{
    
    private Space space;

    public FreezingAir(int density) {
        super(density);
        this.setCharacter('░');
        this.setDescription("Freezing cold air.");
        this.setName("Freezing air");
        this.setfGColor(TileColor.create(180, 180, 255, 255));
        this.setbGColor(TileColor.transparent());
        this.setSpreadFactor(0.5);
        setTileName("Freezing Air");
        this.disapparationRate = 10;
    }

    @Override
    public Space getSpace() {
        return space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    protected Gas createSelf(int density) {
        return new FreezingAir(density);
    }

    @Override
    public Liquid getCondensationLiquid(int depth) {
        return null;
    }

    @Override
    public void behave() {
        List<Terrain> terrains = getSpace().getTerrains();
        Stack<Terrain> terrainsToBeAdded = new Stack<Terrain>();
        for (Terrain terrain : terrains) {
            if (terrain instanceof Liquid liquid){
                terrainsToBeAdded.add(liquid.getFreezingTerrain(liquid.getDepth()));
                liquid.subtractDepth(liquid.getDepth());
            }
            if (terrain instanceof Gas gas && gas.condenses()){
                terrainsToBeAdded.add(gas.getCondensationLiquid(gas.getDensity()));
                gas.removeDensity((gas.getDensity()));
            }
        }
        while (!terrainsToBeAdded.isEmpty()) {
            getSpace().addTerrain(terrainsToBeAdded.pop());
        }
        if (getSpace().isOccupied()){
            Entity occupant = getSpace().getOccupant();
            occupant.addStatus(new Freezing());
        }
        super.behave();
    }

    @Override
    public boolean condenses() {
        return false;
    }
    
}
