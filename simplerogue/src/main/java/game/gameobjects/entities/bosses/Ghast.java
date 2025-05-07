package game.gameobjects.entities.bosses;

import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.PathConditions;
import game.gamelogic.DropsXP;
import game.gamelogic.HasDodge;
import game.gamelogic.HasInventory;
import game.gamelogic.HasResistances;
import game.gamelogic.behavior.AnimalBehavior;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHitted;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.terrains.gasses.Miasma;

public class Ghast extends Animal implements HasInventory, DropsXP, HasResistances, OnHitted, HasDodge{

    private List<Item> inventory = new ArrayList<>();
    private List<Resistance> resistances = new ArrayList<>();

    @Override
    public List<Item> getInventory() {
        return this.inventory;
    }

    @Override
    public int getHardWeightLimit() {
        return 20;
    }

    public Ghast() {
        super(TileColor.transparent(), TileColor.create(141, 30, 232, 255),'G');
        setMaxHP(25);
        setHP(25);
        setWeight(20);
        setName("Ghast");
        setDescription("A shambling, bloated undead. It stinks.");
        setCorpse(null);
        setBehavior(new GhastBehavior(this));

        Weapon claws = new Weapon();
        claws.setName("Claws");
        claws.setDamageType(DamageType.SLASHING);
        claws.setDamage(2, 6);
        setUnarmedWeapon(claws);

        this.resistances.add(new PercentageResistance(DamageType.SUFFICATION, 1.00));
        this.resistances.add(new PercentageResistance(DamageType.POISON, 1.00));
    }

    @Override
    public int dropXP() {
        return 25;
    }

    public class GhastBehavior extends AnimalBehavior {

        public GhastBehavior(Entity animal) {
            super(animal);
        }

        @Override
        public int behave() {
            if (randomNumber(0,1) == 1) {
                animal.getSpace().addGas(new Miasma(randomNumber(1,5)));
            }
            return super.behave();
        }

        @Override
        protected PathConditions generateConditionsToSpace() {
            return new PathConditions()
                .addDeterrentConditions((space) -> {
                    if (space.getTerrains().size() == 1 && space.getTerrains().get(0) instanceof Miasma) {
                        return 0d;
                    } else if (!space.getTerrains().isEmpty()) {
                        return 10d;
                    }
                    return 0d;
                }
            );
        }
        
    }

    @Override
    public List<Resistance> getResistances() {
        return this.resistances;
    }

    @Override
    public void doOnHitted(Entity self, Entity other, AttackInfo attackInfo) {
        self.getSpace().addGas(new Miasma(randomNumber(1,5)));
    }

    @Override
    public int getDodge() {
        return 2;
    }
}
