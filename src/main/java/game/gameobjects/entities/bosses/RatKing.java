package game.gameobjects.entities.bosses;

import static game.App.getRandom;
import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.gamelogic.DropsXP;
import game.gamelogic.HasInventory;
import game.gamelogic.HasResistances;
import game.gamelogic.Levelable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnDeath;
import game.gamelogic.resistances.RangeResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.Rat;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Sleeping;
import game.gameobjects.statuses.Status;

public class RatKing extends Animal implements HasInventory, DropsXP{

    private List<Item> inventory = new ArrayList<>();
    private int ratCount = 0;
    private int RAT_MAX = 10;
    private int RAT_PREP = 3;

    public RatKing() {
        super(TileColor.transparent(), TileColor.create(125, 76, 36, 255),'R');
        setBaseMaxHP(17);
        setHP(17);
        setWeight(12);
        setName("Rat King");
        setDescription("A mass of rats, bound together by a strange gelatinous substance. Every once in a while, a rat will free itself from the pile.");

        Weapon fangs = new Weapon();
        fangs.setName("Fangs");
        fangs.setDamageType(DamageType.PIERCING);
        fangs.setDamage(1, 4);
        setUnarmedWeapon(fangs);
    }

    @Override
    public int behave(){
        if (randomNumber(0,2) == 2 && ((ratCount < RAT_MAX && this.isWithinVision(Dungeon.getCurrentFloor().getPlayer())) || ratCount < RAT_PREP)) {
            Space space = getRandomSpace();
            if (space != null) {
                space.setOccupant(RatKing.this.new SummonedRat());
                ratCount++;
            }
        }
        if (randomNumber(0,1) == 1) {
            Space space = getRandomSpace();
            if (space != null) {
                Space.moveEntity(this,space);
                return this.getTimeToMove();
            }
        } else {
            return super.behave();
        }
        return this.getTimeToWait();
    }

    private Space getRandomSpace(){
        List<Space> list = Space.getAdjacentSpaces(this.getSpace());
        for (int i = 0; i < list.size(); i++) {
            Space space = list.get(i);
            if (space.isOccupied()) {
                list.remove(space);
                i--;
            }
        }
        if (list.size() > 0) {
            return getRandom(list);
        }
        return null;
    }

    @Override
    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public int getHardWeightLimit() {
        return 20;
    }

    public class SummonedRat extends Rat implements Levelable, HasResistances, OnDeath {

        public SummonedRat() {
            super();
            setDescription(getDescription() + " it looks scraggly and malnurished, yet strangely coordinated with the other rats.");
            int hp = randomNumber(2,5);
            setBaseMaxHP(hp);
            setHP(hp);

            Weapon fangs = new Weapon();
            fangs.setName("fangs");
            fangs.setDamageType(DamageType.PIERCING);
            fangs.setDamage(1, 3);
            setUnarmedWeapon(fangs);

            Iterator<Status> it = this.getStatuses().iterator();
            while (it.hasNext()) {
                Status status = it.next();
                if (status instanceof Sleeping) {
                    it.remove();
                }
            }

        }

        @Override
        public Item getCorpse() {
            return null;
        }

        @Override
        public int behave() {
            getUnarmedWeapon().setLevel(getLevel());
            return super.behave();
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
            return List.of(
                new RangeResistance(DamageType.BLUNT, this, 0,1),
                new RangeResistance(DamageType.PIERCING, this, 0,1),
                new RangeResistance(DamageType.SLASHING, this, 0,1)
            );
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
