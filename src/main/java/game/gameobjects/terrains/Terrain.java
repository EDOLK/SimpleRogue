package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.DisplayableTile;

public class Terrain extends DisplayableTile{

    private boolean sightBlocker;

    public boolean isSightBlocker(){
        return this.sightBlocker;
    }

    public void setSightBlocker(boolean sightBlocker){
        this.sightBlocker = sightBlocker;
    }

    public Terrain(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
    }

    public Terrain(){
        super();
    }
}
