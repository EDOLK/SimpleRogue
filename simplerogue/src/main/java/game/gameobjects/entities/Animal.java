package game.gameobjects.entities;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.AttributeMap;
import game.gamelogic.HasAttributes;
import game.gamelogic.behavior.Behavior;
import game.gamelogic.behavior.HasBehavior;

public abstract class Animal extends Entity implements HasBehavior, HasAttributes {

    private Behavior behavior;
    private AttributeMap aMap = new AttributeMap();

    public Animal(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
    }

    public Animal() {
        super();
    }

    @Override
    public Behavior getBehavior() {
        return this.behavior;
    }

    @Override
    public boolean setBehavior(Behavior behavior) {
        this.behavior = behavior;
        return true;
    }

    @Override
    public AttributeMap getMap() {
        return this.aMap;
    }

}
