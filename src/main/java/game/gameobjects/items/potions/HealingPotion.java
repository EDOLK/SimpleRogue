package game.gameobjects.items.potions;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.gamelogic.Consumable;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Healing;

public class HealingPotion extends Item implements Consumable{
    
    public HealingPotion(){
        setCharacter('p');
        setfGColor(TileColor.create(255, 5, 5, 255));
        setbGColor(TileColor.transparent());
        setDescription("A red potion, it looks... healthy?");
        setName("Healing Potion");
        setTileName("Red Potion");
        setWeight(1);
    }

    @Override
    public boolean consume(Entity consumer) {
        if (consumer.addStatus(new Healing((int)Math.floor(consumer.getMaxHP() * 0.60), 1, 3))){
            if (consumer instanceof PlayerEntity){
                Display.log("You drink the " + getName() + ".");
            } else {
                Display.log("The " + consumer.getName() + " drinks the " + getName() + ".", consumer.getSpace());
            }
            Display.update();
            return true;
        }
        return false;
    }

}