package game.gameobjects;

public enum DamageType {

    SLASHING,
    PIERCING,
    BLUNT,
    FROST,
    FIRE,
    POISON,
    BLEED,
    SUFFICATION; 

    @Override 
    public String toString(){ 
        return this.name().toLowerCase(); 
    }

}
