package game.gameobjects.terrains;

import game.gameobjects.terrains.liquids.Liquid;

public interface Melts {
    public abstract Liquid getMeltingLiquid(int depth);
    public abstract boolean melts();
}
