package game.gameobjects.entities.bosses;

import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.floorgeneration.Pools;
import game.gamelogic.DropsXP;
import game.gamelogic.HasInventory;
import game.gamelogic.HasResistances;
import game.gamelogic.Levelable;
import game.gamelogic.behavior.AnimalBehavior;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnDeath;
import game.gamelogic.resistances.RangeResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.Rat;
import game.gameobjects.items.Corpse;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;

public class RatKing extends Animal implements HasInventory, DropsXP{

    private List<Item> inventory = new ArrayList<>();
    private int ratCount = 0;
    private int RAT_MAX = 10;
    private int RAT_PREP = 3;

    public RatKing() {
        super(TileColor.transparent(), TileColor.create(125, 76, 36, 255),'R');
        setMaxHP(17);
        setHP(17);
        setWeight(12);
        setName("Rat King");
        setDescription("A mass of rats, bound together by a strange gelatinous substance. Every once in a while, a rat will free itself from the pile.");
        setCorpse(new Corpse(new Rat()));
        setBehavior(new RatKingBehavior(this));

        Weapon fangs = new Weapon();
        fangs.setName("Fangs");
        fangs.setDamageType(DamageType.PIERCING);
        fangs.setDamage(1, 4);
        setUnarmedWeapon(fangs);
    }

    public class RatKingBehavior extends AnimalBehavior {

        public RatKingBehavior(Entity animal) {
            super(animal);
        }

        @Override
        public void behave() {
            if (randomNumber(0,1) == 1) {
                Space space = getRandomSpace();
                if (space != null) {
                    Space.moveEntity(animal,space);
                }
            } else {
                super.behave();
            }
            if (randomNumber(0,2) == 2 && (ratCount < RAT_MAX && animal.isWithinVision(Dungeon.getCurrentFloor().getPlayer()) || ratCount < RAT_PREP)) {
                Space space = getRandomSpace();
                if (space != null) {
                    ratCount++;
                    space.setOccupant(RatKing.this.new SummonedRat());
                }
            }
        }

        private Space getRandomSpace(){
            List<Space> list = Space.getAdjacentSpaces(animal.getSpace());
            for (int i = 0; i < list.size(); i++) {
                Space space = list.get(i);
                if (space.isOccupied()) {
                    list.remove(space);
                    i--;
                }
            }
            if (list.size() > 0) {
                return Pools.getRandom(list);
            }
            return null;
        }
        
    }

    @Override
    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public int getMaxWeight() {
        return 20;
    }

    public class SummonedRat extends Rat implements Levelable, HasResistances, OnDeath {

        private List<Resistance> resistances = new ArrayList<>(List.of(
            new RangeResistance(DamageType.BLUNT, this, 0,1),
            new RangeResistance(DamageType.PIERCING, this, 0,1),
            new RangeResistance(DamageType.SLASHING, this, 0,1)
        ));

        public SummonedRat() {
            super();
            setDescription(getDescription() + " it looks scraggly and malnurished, yet strangely coordinated with the other rats.");
            int hp = randomNumber(2,5);
            setMaxHP(hp);
            setHP(hp);
            setCorpse(null);

            Weapon fangs = new Weapon();
            fangs.setName("fangs");
            fangs.setDamageType(DamageType.PIERCING);
            fangs.setDamage(1, 3);
            setUnarmedWeapon(fangs);
        }

        @Override
        public void behave() {
            getUnarmedWeapon().setLevel(getLevel());
            super.behave();
        }

        @Override
        public int dropXP() {
            return 0;
        }

        @Override
        public int getLevel() {
            int l = 0;
            for (Space space : Space.getAdjacentSpaces(getSpace())) {
                if (space.isOccupied() && space.getOccupant() instanceof SummonedRat) {
                    l++;
                }
            }
            return l;
        }

        @Override
        public boolean setLevel(int level) {
            return false;
        }

        @Override
        public List<Resistance> getResistances() {
            return resistances;
        }

        @Override
        public void doOnDeath(Entity self, Entity other, AttackInfo attackInfo) {
            RatKing.this.ratCount--;
        }

        @Override
        public int getDropPoints() {
            return 0;
        }
    }

    @Override
    public int dropXP() {
        return 25;
    }

}
