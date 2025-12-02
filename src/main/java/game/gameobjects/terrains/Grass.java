package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Triggerable;
import game.gameobjects.entities.Entity;

public class Grass extends Moss implements Triggerable{

    public Grass() {
        setCharacter('▓');
        setBgColor(TileColor.transparent());
        setFgColor(TileColor.create(40,120,40,255));
    }

    @Override
    public boolean isSightBlocker() {
        return true;
    }

    @Override
    public String getName() {
        return "Tall Grass";
    }

    @Override
    public String getDescription() {
        return "Tall grass growing through the cracks of the dungeon floor.";
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
