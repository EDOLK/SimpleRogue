package game.gameobjects.items;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.display.Display;
import game.gamelogic.Consumable;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.statuses.Poisoned;

public class Ration extends Item implements Consumable {
    public Ration() {
        super();
        setName("Ration");
        setDescription("Standard rations, these are quite old and you're not sure if they're still good.");
        setCharacter('r');
        setWeight(1);
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(250,250,200,255));
    }
    @Override
    public boolean consume(Entity consumer) {
        if (Math.random() <= 0.25) {
            consumer.addStatus(new Poisoned(0, 1, 5));
            if (consumer instanceof PlayerEntity) {
                Display.log("That ration was way too old.");
            }
        } else {
            consumer.heal(App.randomNumber(1,3));
            if (consumer instanceof PlayerEntity) {
                Display.log("Still edible, suprisingly.");
            }
        }
        return true;
    }

}
