package game.gamelogic;

import org.hexworks.zircon.api.data.Tile;

import game.gameobjects.DisplayableTile;

public interface Examinable extends HasName{
    public String getDescription();
    public Tile getTile();
    default DisplayableTile getAsDisplayableTile(){
        return (DisplayableTile)this;
    }
}
