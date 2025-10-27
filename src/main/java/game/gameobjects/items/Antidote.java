package game.gameobjects.items;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.gamelogic.Consumable;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.PoisonImmune;

public class Antidote extends Item implements Consumable {

    public Antidote() {
        super();
        setName("Antidote");
        setDescription("A small vial of bitter green medicine.");
        setCharacter('a');
        setWeight(1);
        setBgColor(TileColor.transparent());
        setFgColor(TileColor.create(150,250,150,255));
    }

    @Override
    public boolean consume(Entity consumer) {
        consumer.addStatus(new PoisonImmune(App.randomNumber(5, 15)));
        return true;
    }

    
}
