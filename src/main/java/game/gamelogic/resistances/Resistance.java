package game.gamelogic.resistances;

import game.gamelogic.AbstractDamageModifier;
import game.gamelogic.Levelable;
import game.gameobjects.DamageType;

public abstract class Resistance extends AbstractDamageModifier {

    public Resistance(DamageType type){
        super(type);
    }

    public Resistance(DamageType type, Levelable levelable){
        super(type, levelable);
    }
}
