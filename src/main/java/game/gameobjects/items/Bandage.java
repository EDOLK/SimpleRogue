package game.gameobjects.items;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.display.Display;
import game.gamelogic.Consumable;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.statuses.Bleeding;

public class Bandage extends Item implements Consumable {
    public Bandage() {
        super();
        setName("Bandage");
        setDescription("A couple of old bandages, should be good for minor wounds.");
        setCharacter('b');
        setWeight(1);
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(250,250,200,255));
    }
    @Override
    public boolean consume(Entity consumer) {
        consumer.heal(App.randomNumber(1,2));
        Bleeding bleeding = (Bleeding)consumer.getStatusByClass(Bleeding.class);
        if (bleeding != null) {
            consumer.removeStatus(bleeding);
            if (consumer instanceof PlayerEntity) {
                Display.log("The Bandage staves some of your bleeding.");
            }
        }
        return true;
    }
    
}
