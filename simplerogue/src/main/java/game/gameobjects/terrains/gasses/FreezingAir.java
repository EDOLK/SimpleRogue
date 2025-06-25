package game.gameobjects.terrains.gasses;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Freezing;
import game.gameobjects.terrains.liquids.Liquid;

public class FreezingAir extends Gas{
    
    private Space space;

    public FreezingAir(int density) {
        super(density);
        this.setCharacter(' ');
        this.setDescription("Freezing cold air.");
        this.setName("Freezing air");
        this.setfGColor(TileColor.transparent());
        this.setbGColor(TileColor.create(180, 180, 255, 255));
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
        throw new UnsupportedOperationException("Unimplemented method 'getCondensationLiquid'");
    }

    @Override
    public int behave() {
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
