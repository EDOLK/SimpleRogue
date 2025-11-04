package game.gameobjects.entities;


import java.util.List;
import java.util.stream.Collectors;

import org.hexworks.zircon.api.color.TileColor;

import game.PathConditions;
import game.gamelogic.AttributeMap;
import game.gamelogic.HasAttributes;
import game.gamelogic.IsDeterrent;
import game.gamelogic.IsForbidden;
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
        addStatus(new Sleeping());
    }

    protected Behavior getDefaultBehavior(){
        return new AnimalWandering(this);
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
        return new PathConditions()
            .addDeterrentConditions(
                (space) -> {
                    double det = 0.0d;
                    List<IsDeterrent> deterrents = space.getTerrains().stream()
                        .filter((terrain) -> terrain instanceof IsDeterrent)
                        .map(terrain -> (IsDeterrent)terrain)
                        .collect(Collectors.toList());
                    for (IsDeterrent isDeterrent : deterrents) {
                        det += isDeterrent.getDeterrent(this);
                    }
                    return det;
                }
            )
            .addForbiddenConditions(
                (space) -> {
                    boolean forb = false;
                    List<IsForbidden> forbiddens = space.getTerrains().stream()
                        .filter(terrain -> terrain instanceof IsForbidden)
                        .map(terrain -> (IsForbidden)terrain)
                        .collect(Collectors.toList());
                    for (IsForbidden isForbidden : forbiddens) {
                        forb = forb | isForbidden.getForbidden(this);
                    }
                    return forb;
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
