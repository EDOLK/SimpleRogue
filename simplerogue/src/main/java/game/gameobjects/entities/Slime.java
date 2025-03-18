package game.gameobjects.entities;

import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.display.Display;
import game.floorgeneration.Pool;
import game.gamelogic.Consumable;
import game.gamelogic.DropsXP;
import game.gamelogic.HasDodge;
import game.gamelogic.HasDrops;
import game.gamelogic.HasInventory;
import game.gamelogic.HasResistances;
import game.gamelogic.behavior.AnimalBehavior;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnDeath;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.RangeResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.items.Corpse;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;

public class Slime extends Animal implements DropsXP, HasDodge, HasResistances, HasInventory, HasDrops, OnDeath{

    private ArrayList<Resistance> resistances = new ArrayList<Resistance>();
    private ArrayList<Item> inventory = new ArrayList<Item>();
    private int dropPoints = randomNumber(3, 15);
    
    public Slime(){

        super(TileColor.transparent(), TileColor.create(20, 230, 20, 255), 'o');
        setMaxHP(10);
        setHP(10);
        setName("Green Slime");
        setTileName("Slime");
        setDescription("A giant slime.");
        setWeight(5);
        setBehavior(new AnimalBehavior(this));

        Weapon mass = new Weapon();
        mass.setName("mass");
        mass.setDamageType(DamageType.BLUNT);
        mass.setDamage(1, 3);
        setUnarmedWeapon(mass);
        
        setCorpse(null);

        resistances.add(new RangeResistance(DamageType.BLUNT, 0, 3));
        resistances.add(new PercentageResistance(DamageType.POISON, 0.20));

    }
    
    @Override
    public void behave(){
        if (!getSpace().getItems().isEmpty()){
            Item item = getSpace().getItems().get(randomNumber(0, getSpace().getItems().size()-1));
            addItemToInventory(item);
            getSpace().remove(item);
            Display.log("The " + item.getName() + " becomes suspended in the " + getName(), getSpace());
            setMaxHP(getMaxHP() + 1);
            setHP(getHP()+1);
        }
        if (randomNumber(1, 4) == 1){
            Item consumableItem = null;
            for (Item item : inventory) {
                if (item instanceof Consumable){
                    consumableItem = item;
                    break;
                }
            }
            if (consumableItem != null && consumableItem instanceof Consumable consumable){
                consumable.consume(this);
                inventory.remove(consumableItem);
                setMaxHP(getMaxHP()-1);
                setHP(getHP()-1);
            }
        }
        super.behave();
    }

    @Override
    public ArrayList<Resistance> getResistances() {
        return resistances;
    }

    @Override
    public int getDodge() {
        return 3;
    }

    @Override
    public int dropXP() {
        return 10;
    }

    @Override
    public ArrayList<Item> getInventory() {
        return inventory;
    }

    @Override
    public int getMaxWeight() {
        return 999;
    }

    @Override
    public String getDescription() {
        if (!getInventory().isEmpty()){
            if (getInventory().size() > 1){
                return super.getDescription() + " You can see multiple items floating within it.";
            } else {
                return super.getDescription() + " You can see a " + getInventory().get(0).getName() + " floating within it.";
            }
        }
        return super.getDescription();
    }

    @Override
    public void doOnDeath(Entity self, Entity other, AttackInfo attackInfo) {
        if (attackInfo.getDamageType() == DamageType.FIRE) {
            getSpace().setOccupant(new EvaporatedSlime());
        } else {
            getSpace().addItem(new Corpse(this));
        }
    }

    @Override
    public Pool<Supplier<Item>> getItemPool() {
        return Dungeon.getCurrentDropPool();
    }

    @Override
    public int getDropPoints() {
        return dropPoints;
    }

    @Override
    public void setDropPoints(int points) {
        this.dropPoints = points;
    }


}
