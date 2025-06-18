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
        this.setSpreadFactor(0.5f);
        this.setTileName("Freezing Air");
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
    public int behave() {
        List<Terrain> terrains = getSpace().getTerrains();
        Stack<Terrain> terrainsToBeAdded = new Stack<Terrain>();
        for (Terrain terrain : terrains) {
            if (terrain instanceof Liquid liquid && liquid.evaporates()){
                int a = Math.min(this.getAmount(), liquid.getAmount());
                this.setAmount(this.getAmount()-a);
                liquid.setAmount(liquid.getAmount()-a);
                terrainsToBeAdded.add(liquid.getFreezeTerrain(a));
            }
            if (terrain instanceof Gas gas && gas.condenses()){
                int a = Math.min(this.getAmount(), gas.getAmount());
                this.setAmount(this.getAmount()-a);
                gas.setAmount(gas.getAmount()-a);
                terrainsToBeAdded.add(gas.getCondensationLiquid(a));
            }
        }
        while (!terrainsToBeAdded.isEmpty()) {
            getSpace().addTerrain(terrainsToBeAdded.pop());
        }
        if (getSpace().isOccupied()){
            Entity occupant = getSpace().getOccupant();
            occupant.addStatus(new Freezing());
        }
        return super.behave();
    }

    @Override
    public boolean condenses() {
        return false;
    }
    
}
