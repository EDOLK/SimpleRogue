package game.gameobjects.items;

import static game.App.lerp;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Consumable;
import game.gameobjects.entities.Entity;

public class CookedCorpse extends Item implements Consumable{

    private double satiety;
    
    public CookedCorpse(Corpse corpse){
        this.satiety = lerp(corpse.getDecayLimit(), 0, 0, 1.0, corpse.getDecay());
        setName("Cooked " + corpse.getOriginalEntityName());
        setCharacter(corpse.getCharacter());
        setfGColor(TileColor.create(255, 170, 84,255));
    }

    @Override
    public boolean consume(Entity consumer) {
        consumer.heal((int)(consumer.getMaxHP()*satiety));
        return true;
    }

}
