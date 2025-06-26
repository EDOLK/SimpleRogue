package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Examinable;
import game.gamelogic.Flammable;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.terrains.liquids.Water;

public class Moss extends Terrain implements Flammable, Examinable, SelfAware, Behavable{

    private Space space;

    public Moss() {
        setCharacter('░');
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(40,120,40,255));
    }

    @Override
    public String getName() {
        return "Moss";
    }

    @Override
    public String getDescription() {
        return "Moss growing through the cracks of the dungeon floor.";
    }

    @Override
    public int getFuelValue() {
        return 1;
    }

    @Override
    public void onBurn() {
        getSpace().addTerrain(new Ashes());
        getSpace().remove(this);
    }

    @Override
    public Space getSpace() {
        return this.space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public int behave() {
        for (Terrain t : getSpace().getTerrains()) {
            if (t instanceof Water w) {
                w.setAmount(w.getAmount()-1);
                getSpace().addTerrain(new Grass());
                getSpace().remove(this);
                break;
            }
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return getSpace() != null;
    }

    
}
