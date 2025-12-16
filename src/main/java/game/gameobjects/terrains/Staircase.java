package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.gamelogic.Examinable;
import game.gamelogic.SelfAware;
import game.gamelogic.interactions.HasInteraction;
import game.gamelogic.interactions.Interaction;
import game.gamelogic.interactions.InteractionResult;
import game.gameobjects.Space;

public class Staircase extends Terrain implements SelfAware, HasInteraction, Examinable{

    private Space space;
    private String name = "Staircase";
    private String description = "A staircase.";
    
    public Staircase(){
        setCharacter('>');
        setBgColor(TileColor.transparent());
        setFgColor(TileColor.create(255, 255, 255, 255));
        setTileName("Staircase Down");
    }

    @Override
    public Space getSpace() {
        return space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Interaction getInteraction() {
        return new Interaction.Builder()
            .withName("Descend")
            .withOnInteract((interactor) -> {
                Dungeon.goDownFloor(Staircase.this);
                return InteractionResult.create();
            })
            .build();
    }
    
}
