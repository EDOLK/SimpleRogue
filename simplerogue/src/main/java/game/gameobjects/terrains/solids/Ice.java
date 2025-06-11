package game.gameobjects.terrains.solids;

import static game.App.randomNumber;

import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.Dungeon;
import game.display.Display;
import game.gamelogic.Examinable;
import game.gamelogic.SelfAware;
import game.gamelogic.Triggerable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.terrains.liquids.Liquid;
import game.gameobjects.terrains.liquids.Water;

public class Ice extends Solid implements Triggerable, SelfAware, Examinable{

    private Space space;
    private String name;
    private String description;
    
    public Ice(int amount){
        super(amount);
        name = "Ice";
        description = "Ice. Slippery.";
        setbGColor(TileColor.create(150, 245, 255, 100));
        setfGColor(TileColor.transparent());
        setCharacter(' ');
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
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
        if (randomNumber(0, 1) == 1){
            if (entity instanceof PlayerEntity){
                Display.log("You slip on the ice.");
            } else {
                Display.log("The " + entity.getName() + " slips on the ice.", getSpace());
            }
            Space random = App.getRandom(Space.getAdjacentSpaces(getSpace()));
            if (random != null) {
                Space.moveEntity(entity, random);
            }
        }
    }

    @Override
    public boolean triggerableWhenAdding() {
        return false;
    }

    @Override
    public Liquid getMeltingLiquid(int depth) {
        return new Water(depth);
    }

    @Override
    public boolean melts() {
        return true;
    }
    
}
