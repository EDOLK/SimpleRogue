package game.gameobjects.entities;

import static game.App.getRandom;
import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.display.Display;
import game.floorgeneration.pools.Pool;
import game.gamelogic.Consumable;
import game.gamelogic.DropsXP;
import game.gamelogic.HasDodge;
import game.gamelogic.HasDrops;
import game.gamelogic.HasInventory;
import game.gamelogic.HasResistances;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnDeath;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.RangeResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Slimed;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.liquids.SlimeLiquid;

public class Slime extends Animal implements DropsXP, HasDodge, HasResistances, HasInventory, HasDrops, OnDeath{

    private List<Item> inventory = new ArrayList<>();
    
    public Slime(){

        super(TileColor.transparent(), TileColor.create(20, 230, 20, 255), 'o');
        setBaseMaxHP(10);
        setHP(10);
        setName("Green Slime");
        setTileName("Slime");
        setDescription("A giant slime.");
        setWeight(5);

        Weapon mass = new Weapon();
        mass.setName("mass");
        mass.setDamageType(DamageType.BLUNT);
        mass.setDamage(1, 3);
        setUnarmedWeapon(mass);
        
    }

    @Override
    public Item getCorpse(){
        return null;
    }
    
    @Override
    public int behave(){
        if (!getSpace().getItems().isEmpty()){
            Item item = getRandom(getSpace().getItems());
            if (addItemToInventory(item)){
                getSpace().remove(item);
                Display.log("The " + item.getName() + " becomes suspended in the " + getName(), getSpace());
                setBaseMaxHP(getMaxHP() + 1);
                setHP(getHP()+1);
            }
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
                setBaseMaxHP(getMaxHP()-1);
                setHP(getHP()-1);
            }
        }
        return super.behave();
    }

    @Override
    public List<Resistance> getResistances() {
        return List.of(
            new RangeResistance(DamageType.BLUNT, 0, 3),
            new PercentageResistance(DamageType.POISON, 0.20)
        );
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
    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public int getHardWeightLimit() {
        return 20;
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
            getSpace().addTerrain(new SlimeLiquid(1));
            Space.getAdjacentSpaces(getSpace()).forEach((s) -> {
                if (Math.random() < .50) {
                    s.addTerrain(new SlimeLiquid(1));
                }
            });
        }
    }

    @Override
    public Pool<Item> getItemPool() {
        return Dungeon.getCurrentDropPool();
    }

    @Override
    public int getDropPoints() {
        return randomNumber(3, 15);
    }

    @Override
    protected boolean isVulnerable(Status status) {
        if (status instanceof Slimed) {
            return false;
        }
        return super.isVulnerable(status);
    }

}
