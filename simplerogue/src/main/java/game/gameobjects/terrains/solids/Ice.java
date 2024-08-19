package game.gameobjects.terrains.solids;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

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
        setTileName("Ice");
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
            Space[] potentialSpaces = new Space[8];
            int index = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0){
                        continue;
                    }
                    potentialSpaces[index] = Dungeon.getCurrentFloor().getSpace(getX() + i, getY() + j);
                    index++;
                }
            }
            Space moveSpace = potentialSpaces[randomNumber(0, 7)];
            Space.moveEntity(entity, moveSpace);
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