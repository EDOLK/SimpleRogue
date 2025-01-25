package game.gameobjects.entities;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.behavior.Behavior;
import game.gamelogic.behavior.HasBehavior;

public abstract class Animal extends Entity implements HasBehavior {

    private Behavior behavior;

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

}
