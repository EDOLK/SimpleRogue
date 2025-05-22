package game.gameobjects.entities;

import static game.App.randomNumber;

import java.util.ArrayList;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Consumable;
import game.gamelogic.DropsXP;
import game.gamelogic.HasDodge;
import game.gamelogic.HasResistances;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Burning;
import game.gameobjects.statuses.Flying;
import game.gameobjects.statuses.Status;

public class EvaporatedSlime extends Animal implements HasResistances, HasDodge, DropsXP {
    
    ArrayList<Resistance> resistances = new ArrayList<Resistance>();

    public EvaporatedSlime(){
        super(TileColor.transparent(), TileColor.create(20, 230, 20, 255), '▒');
        setMaxHP(10);
        setHP(10);
        setName("Evaporated Slime");
        setTileName("Evaporated Slime");
        setDescription("A cloud of green vapor, still somehow keeping itself together.");
        setWeight(0);
        setCorpse(null);

        Weapon mass = new Weapon();
        mass.setName("mass");
        mass.setDamageType(DamageType.SUFFICATION);
        mass.setDamage(1, 3);
        setUnarmedWeapon(mass);
        
        Flying flying = new Flying();
        flying.setPerminent(true);
        addStatus(flying);

        getResistances().add(new PercentageResistance(DamageType.FIRE, 0.50));
        getResistances().add(new PercentageResistance(DamageType.SLASHING, 0.50));
        getResistances().add(new PercentageResistance(DamageType.PIERCING, 0.50));
    }
    
    @Override
    protected boolean isVulnerable(Class<? extends Status> status) {
        if (status == Burning.class){
            return false;
        }
        return super.isVulnerable(status);
    }

    @Override
    public ArrayList<Resistance> getResistances() {
        return resistances;
    }

    @Override
    public int behave() {
        if (randomNumber(1, 4) == 1){
            for (Item item : getSpace().getItems()) {
                if (item instanceof Consumable consumable){
                    consumable.consume(this);
                    break;
                }
            }
        }
        return super.behave();
    }

    @Override
    public int dropXP() {
        return 7;
    }

    @Override
    public int getDodge() {
        return 7;
    }

}
