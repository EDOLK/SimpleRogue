package game.gameobjects.terrains.gasses;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.IsDeterrent;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Freezing;
import game.gameobjects.terrains.liquids.Liquid;

public class FreezingAir extends Gas implements IsDeterrent{
    
    private Space space;

    public FreezingAir(int density) {
        super(density);
        this.setCharacter(' ');
        this.setDescription("Freezing cold air.");
        this.setName("Freezing air");
        this.setFgColor(TileColor.transparent());
        this.setBgColor(TileColor.create(180, 180, 255, 255));
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
            getSpace().getOccupant().addStatus(new Freezing());
        }
        return super.behave();
    }

    @Override
    public boolean condenses() {
        return false;
    }

    @Override
    public double getDeterrent(Entity entity) {
        return entity.isVulnerable(new Freezing()) ? 10d : 0d;
    }
    
}
