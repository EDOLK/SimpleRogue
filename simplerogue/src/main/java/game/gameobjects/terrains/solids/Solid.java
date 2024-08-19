package game.gameobjects.terrains.solids;

import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.liquids.Liquid;

public abstract class Solid extends Terrain{
    public abstract Liquid getMeltingLiquid(int depth);
    public abstract boolean melts();
    public Solid(int depth){
        super();
    }
}
