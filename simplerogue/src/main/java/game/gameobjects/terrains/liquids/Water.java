package game.gameobjects.terrains.liquids;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.gasses.Gas;
import game.gameobjects.terrains.gasses.Steam;
import game.gameobjects.terrains.solids.Ice;

public class Water extends Liquid{
    
    public Water(int depth){
        super(depth);
        setCharacter(' ');
        setfGColor(TileColor.transparent());
        setbGColor(TileColor.create(75, 75, 235, 235));
        setName("Water");
        setTileName("Water");
        setViscosity(0);
        this.minOpacity = 100;
        this.maxOpacity = 255;
    }

    @Override
    public Gas getEvaporationGas(int amount) {
        return new Steam(amount);
    }

    @Override
    public Liquid createSelf(int depth) {
        return new Water(depth);
    }

    @Override
    public int behave() {
        return super.behave();
    }

    @Override
    public Terrain getFreezingTerrain(int amount) {
        return new Ice(amount);
    }

    @Override
    public boolean evaporates() {
        return true;
    }

    @Override
    public boolean freezes() {
        return true;
    }

}
