package game.gameobjects.items;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.gamelogic.Consumable;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.statuses.Poisoned;

public class Antidote extends Item implements Consumable {

    public Antidote() {
        super();
        setName("Antidote");
        setDescription("A small green vial of bitter medicine.");
        setCharacter('a');
        setWeight(1);
        setBgColor(TileColor.transparent());
        setFgColor(TileColor.create(150,250,150,255));
    }

    @Override
    public boolean consume(Entity consumer) {
        Poisoned poisoned = (Poisoned)consumer.getStatusByClass(Poisoned.class);
        if (poisoned != null) {
            consumer.removeStatus(poisoned);
            if (consumer instanceof PlayerEntity) {
                Display.log("You feel better.");
            }
            return true;
        }
        return false;
    }

    
}
