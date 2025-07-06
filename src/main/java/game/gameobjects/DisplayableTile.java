package game.gameobjects;

import java.util.HashSet;
import java.util.Set;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.modifier.Modifier;

import game.display.Display;

public abstract class DisplayableTile {

    private TileColor bGColor;
    private TileColor fGColor;
    private char character;
    private Set<Modifier> modifiers = new HashSet<Modifier>();
    private String tileName = "Question Mark";

    public DisplayableTile(String tileName){
        this();
        setTileName(tileName);
    }

    public DisplayableTile(){
        this(TileColor.create(0, 0, 0, 0), TileColor.create(255, 0, 255, 255), '░');
    }

    public DisplayableTile(TileColor bGColor, TileColor fGColor, char character) {
        this.bGColor = bGColor;
        this.fGColor = fGColor;
        this.character = character;
    }

    public TileColor getbGColor() {
        return bGColor;
    }

    public void setbGColor(TileColor bGColor) {
        this.bGColor = bGColor;
    }

    public TileColor getfGColor() {
        return fGColor;
    }

    public void setfGColor(TileColor fGColor) {
        this.fGColor = fGColor;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(Set<Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String name) {
        this.tileName = name;
    }

    public Tile getTile(){
        return getTile(0.0);
    }

    public Tile getTile(double percent){
        switch (Display.getMode()) {
            case ASCII:
                return Tile.newBuilder()
                    .withBackgroundColor(getbGColor().darkenByPercent(percent))
                    .withForegroundColor(getfGColor().darkenByPercent(percent))
                    .withCharacter(getCharacter())
                    .withModifiers(getModifiers())
                    .withTileset(Display.getGraphicalTileSet())
                    .build();
            case GRAPHICAL:
                return Tile.newBuilder()
                    .withName(getTileName())
                    .withTileset(Display.getGraphicalTileSet())
                    .buildGraphicalTile();
            default:
                return Tile.defaultTile();
        }
    }

}
