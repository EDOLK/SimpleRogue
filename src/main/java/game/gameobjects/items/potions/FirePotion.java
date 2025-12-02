package game.gameobjects.items.potions;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.gamelogic.Aimable;
import game.gamelogic.Consumable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Burning;
import game.gameobjects.terrains.Fire;

public class FirePotion extends Item implements Aimable, Consumable{

    public FirePotion(){
        setCharacter('p');
        setFgColor(TileColor.create(250, 134, 7, 255));
        setBgColor(TileColor.transparent());
        setDescription("An orange potion, it seems volatile.");
        setName("Fire Potion");
        setTileName("Orange Potion");
        setWeight(1);
    }

    @Override
    public void onCollision(Space beforeSpace, Space collidingSpace) {
        onLand(collidingSpace);
        collidingSpace.getOccupant().addStatus(new Burning());
    }

    @Override
    public void onLand(Space space) {
        space.addFire(new Fire(5));
    }

    @Override
    public boolean collides(Space space) {
        return space.isOccupied();
    }

    @Override
    public boolean consume(Entity consumer) {
        if (consumer.addStatus(new Burning())){
            if (consumer instanceof PlayerEntity){
                Display.log("You light yourself on fire. Idiot.");
            } else {
                Display.log("The " + consumer.getName() + " lights itself on fire.", consumer.getSpace());
            }
            Display.update();
            return true;
        }
        return false;
    }

}
