package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Examinable;
import game.gamelogic.Flammable;
import game.gamelogic.SelfAware;
import game.gamelogic.Triggerable;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public class Grass extends Terrain implements Flammable, Examinable, SelfAware, Triggerable, Behavable{

    private Space space;

    public Grass() {
        setCharacter('▓');
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(40,120,40,255));
        setSightBlocker(true);
    }

    @Override
    public String getName() {
        return "Grass";
    }

    @Override
    public String getDescription() {
        return "Tall grass growing through the cracks of the dungeon floor.";
    }

    @Override
    public int getFuelValue() {
        return 1;
    }

    @Override
    public int behave() {
        Moss.trySpread(getSpace(), true, true);
        // Space.getAdjacentSpaces(getSpace()).forEach((s) -> Moss.trySpread(s, true, true));
        return 1000;
    }

    @Override
    public boolean isActive() {
        return getSpace() != null;
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
    public void triggerOnEntity(Entity entity) {
        getSpace().addTerrain(new TrampledGrass());
        getSpace().remove(this);
    }

    @Override
    public boolean triggerableWhenAdding() {
        return false;
    }

}
