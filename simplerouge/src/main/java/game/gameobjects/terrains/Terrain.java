package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.DisplayableTile;

public class Terrain extends DisplayableTile{

    public Terrain(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
    }

    public Terrain(){
        super();
    }
}
