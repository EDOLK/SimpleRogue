package game.gameobjects.entities.props;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Tile;

import game.display.Display;
import game.gamelogic.HasResistances;
import game.gamelogic.LightSource;
import game.gamelogic.combat.Attack;
import game.gamelogic.combat.AttackModifier;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Burning;
import game.gameobjects.statuses.FiltersOut;
import game.gameobjects.statuses.Status;
import game.gameobjects.statuses.Wet;
import game.gameobjects.terrains.Fire;

public class Brazier extends Entity implements HasResistances {

    private List<Resistance> resistances;

    public Brazier(boolean lit) {
        this.resistances = new ArrayList<>();
        for (DamageType type : DamageType.values()) {
            if (type != DamageType.BLUNT && type != DamageType.PIERCING && type != DamageType.SLASHING) {
                resistances.add(new PercentageResistance(type, 1.0));
            }
        }
        this.setCharacter('w');
        this.setFgColor(TileColor.create(100,100,100,255));
        this.setBgColor(TileColor.transparent());
        this.setName("Brazier");
        this.setWeight(20);
        this.setBaseMaxHP(1);
        this.setHP(1);
        if (lit) {
            this.addStatus(new Lit());
        } else {
            this.addStatus(new UnLit());
        }
    }

    @Override
    public Item getCorpse() {
        return null;
    }

    @Override
    public List<Resistance> getResistances() {
        return this.resistances;
    }

    @Override
    public int defaultInteraction(Entity interactor) {
        return interactor.getTimeToWait();
    }

    private static class Lit extends Status implements LightSource, AttackModifier, FiltersOut{

        public Lit() {
            super();
            this.setCharacter(' ');
            this.setFgColor(TileColor.transparent());
            this.setBgColor(TileColor.create(255, 255, 0, 50));
            this.setDescriptor("Lit");
        }

        @Override
        public int getLightSourceIntensity() {
            return 10;
        }

        @Override
        public void modifyAttack(Attack attack) {
            attack.attachPostAttackHook((ar) -> {
                if (ar.defender() == owner && !ar.defender().isAlive())
                    ar.defender().getSpace().addTerrain(new Fire(1));
            });
        }

        @Override
        public boolean filterOut(Status status) {
            switch (status) {
                case Wet wet -> {
                    Entity owner = this.owner;
                    owner.removeStatus(this);
                    owner.addStatus(new UnLit());
                    Display.log("The " + owner.getName() + " goes out.", owner.getSpace());
                    return true;
                }
                case Burning burning -> {
                    return true;
                }
                default -> {

                }
            }
            return false;
        }
        
    }

    private static class UnLit extends Status implements FiltersOut{

        public UnLit() {
            super();
            this.setCharacter(' ');
            this.setFgColor(TileColor.transparent());
            this.setBgColor(TileColor.transparent());
            this.setDescriptor("");
        }

        @Override
        public Tile getTile(double percent) {
            return Tile.empty();
        }

        @Override
        public boolean filterOut(Status status) {
            switch (status) {
                case Burning burning -> {
                    Entity owner = this.owner;
                    owner.removeStatus(this);
                    owner.addStatus(new Lit());
                    return true;
                }
                default -> {

                }
            }
            return false;
        }
        
    }

}
