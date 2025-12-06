package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Examinable;
import game.gamelogic.SelfAware;
import game.gameobjects.Space;

public class Ashes extends Terrain implements Examinable, SelfAware {

    private Space space;

    public Ashes() {
        super();
        setCharacter('░');
        setBgColor(TileColor.transparent());
        setFgColor(TileColor.create(120,120,120,255));
    }

    @Override
    public String getName() {
        return "Ashes";
    }

    @Override
    public String getDescription() {
        return "Ashes are scattered across the dungeon floor.";
    }

    @Override
    public Space getSpace() {
        return this.space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    
}
