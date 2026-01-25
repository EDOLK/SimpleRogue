package game.gamelogic;

import game.gameobjects.DamageType;

public abstract class AbstractDamageModifier implements DamageModifier {

    protected DamageType type;
    private Levelable levelable = null;

    public AbstractDamageModifier(DamageType type){
        this(type, null);
    }

    public AbstractDamageModifier(DamageType type, Levelable levelable){
        this.type = type;
        this.levelable = levelable;
    }

    public int getLevel(){
        return levelable != null ? levelable.getLevel() - 1 : 0;
    }

    public DamageType getType() {
        return type;
    }

    public void setType(DamageType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
