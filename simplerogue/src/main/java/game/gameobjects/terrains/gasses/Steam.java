package game.gameobjects.terrains.gasses;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.Space;
import game.gameobjects.terrains.liquids.Liquid;
import game.gameobjects.terrains.liquids.Water;

public class Steam extends Gas{
    
    private Space space;
    
    public Steam(int initialDensity){
        super(initialDensity);
        setCharacter(' ');
        setfGColor(TileColor.transparent());
        setbGColor(TileColor.create(255, 255, 255, 255));
        setName("Steam");
        this.minOpacity = 100;
        this.maxOpacity = 255;
        setSpreadFactor(0.5);
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
        return new Steam(density);
    }

    @Override
    public Liquid getCondensationLiquid(int depth) {
        return new Water(depth);
    }

    @Override
    public boolean condenses() {
        return true;
    }

    
}
