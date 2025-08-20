package game.gameobjects;

import java.util.HashSet;
import java.util.Set;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.modifier.Modifier;

import game.display.Display;

public abstract class DisplayableTile {

    private TileColor bgColor;
    private TileColor fgColor;
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
        this.bgColor = bGColor;
        this.fgColor = fGColor;
        this.character = character;
    }

    public TileColor getBgColor() {
        return bgColor;
    }

    public void setBgColor(TileColor bGColor) {
        this.bgColor = bGColor;
    }

    public TileColor getFgColor() {
        return fgColor;
    }

    public void setFgColor(TileColor fGColor) {
        this.fgColor = fGColor;
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
                    .withBackgroundColor(getBgColor().darkenByPercent(percent))
                    .withForegroundColor(getFgColor().darkenByPercent(percent))
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
