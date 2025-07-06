package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.gamelogic.Examinable;
import game.gamelogic.Interactable;
import game.gamelogic.SelfAware;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public class Staircase extends Terrain implements SelfAware, Interactable, Examinable{

    private Space space;
    private String name = "Staircase";
    private String description = "A staircase.";
    
    public Staircase(){
        setCharacter('>');
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(255, 255, 255, 255));
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
    public void onInteract(Entity interactor) {
        Dungeon.goDownFloor(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
    
}
