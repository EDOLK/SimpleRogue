package game.gamelogic.resistances;

import game.gamelogic.Levelable;
import game.gameobjects.DamageType;

public abstract class Resistance {

    protected DamageType type;
    private Levelable levelable = null;

    public int getLevel(){
        return levelable != null ? levelable.getLevel() : 1;
    }

    public Resistance(DamageType type){
        this.type = type;
    }

    public Resistance(DamageType type, Levelable levelable){
        this.type = type;
        this.levelable = levelable;
    }

    public DamageType getType() {
        return type;
    }

    public void setType(DamageType type) {
        this.type = type;
    }
    

    public abstract int calculateDamage(int damage, DamageType damageType);

    @Override
    public String toString() {
        return type.toString();
    }

}
