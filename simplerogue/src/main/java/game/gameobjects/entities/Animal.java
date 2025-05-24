package game.gameobjects.entities;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

import game.PathConditions;
import game.gamelogic.AttributeMap;
import game.gamelogic.HasAttributes;
import game.gamelogic.behavior.AnimalWandering;
import game.gamelogic.behavior.Behavior;
import game.gamelogic.behavior.HasBehavior;
import game.gamelogic.behavior.HasEnemies;
import game.gameobjects.statuses.Sleeping;

public abstract class Animal extends Entity implements HasBehavior, HasAttributes, HasEnemies{

    private Behavior behavior;
    private AttributeMap aMap = new AttributeMap();

    public Animal(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
        setNightVisionRange(5);
        setBehavior(getDefaultBehavior());
        if (randomNumber(0, 1) == 1){
            addStatus(new Sleeping());
        }
    }

    protected Behavior getDefaultBehavior(){
        return new AnimalWandering(this);
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
    public AttributeMap getAttributeMap() {
        return this.aMap;
    }

    @Override
    public boolean isEnemy(Entity entity){
        return entity instanceof PlayerEntity;
    }

    public PathConditions getConditionsToSpace(){
        return new PathConditions().addDeterrentConditions(
            (space) -> {
                return !space.getTerrains().isEmpty() ? 10d : 0d;
            }
        );
    }

    public PathConditions getConditionsToEntity(){
        return this.getConditionsToSpace();
    }
    
    public int setAndBehave(Behavior newBehavior){
        this.setBehavior(newBehavior);
        if (this.isActive()) {
            return this.behave();
        }
        return this.getTimeToWait();
    }

}
