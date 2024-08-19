package game.gamelogic;

import java.util.List;

import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;

public interface HasResistances{
    public List<Resistance> getResistances();
    default int applyResistances(int damage, DamageType damageType){
        for (Resistance resistance : getResistances()) {
            damage = resistance.calculateDamage(damage, damageType);
        }
        return Math.max(0, damage);
    }
}
