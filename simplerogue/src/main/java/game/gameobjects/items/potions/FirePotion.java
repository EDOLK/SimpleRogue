package game.gameobjects.items.potions;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
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
        setfGColor(TileColor.create(250, 134, 7, 255));
        setbGColor(TileColor.transparent());
        setDescription("An orange potion, it seems volitile.");
        setName("Orange Potion");
        setWeight(1);
    }

    @Override
    public void onHit(Entity target) {
        target.addStatus(new Burning(1, 3, 5, 0, 1));
    }

    @Override
    public void onLand(Space space) {
        space.addFire(new Fire(5));
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                try {
                    Space pSpace = Dungeon.getCurrentFloor().getSpace(space.getX()+i, space.getY()+j);
                    if (pSpace.isOccupied() && !pSpace.getOccupant().isGasBlocker() || (!pSpace.isOccupied())){
                        pSpace.addFire(new Fire(3));
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    @Override
    public boolean landsOnHit() {
        return true;
    }

    @Override
    public boolean consume(Entity consumer) {
        if (consumer.addStatus(new Burning())){
            if (consumer instanceof PlayerEntity){
                Display.log("You light yourself on fire. Idiot.");
            } else{
                Display.log("The " + consumer.getName() + " lights itself on fire.", consumer.getSpace());
            }
            Display.update();
            return true;
        }
        return false;
    }

}
