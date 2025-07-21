package game.gameobjects.entities.props;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Tile;

import game.display.Display;
import game.gamelogic.HasResistances;
import game.gamelogic.LightSource;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnDeath;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Burning;
import game.gameobjects.statuses.SeperateOut;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.Fire;
import game.gameobjects.terrains.liquids.Water.Wet;

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
        this.setfGColor(TileColor.create(100,100,100,255));
        this.setbGColor(TileColor.transparent());
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

    private static class Lit extends Status implements LightSource, OnDeath, SeperateOut{

        public Lit() {
            super();
            this.setCharacter(' ');
            this.setfGColor(TileColor.transparent());
            this.setbGColor(TileColor.create(255, 255, 0, 50));
            this.setDescriptor("Lit");
        }

        @Override
        public int getLightSourceIntensity() {
            return 10;
        }

        @Override
        public void doOnDeath(Entity self, Entity other, AttackInfo attackInfo) {
            self.getSpace().addTerrain(new Fire(1));
        }

        @Override
        public boolean onStackOut(Status sameStatus) {
            if (sameStatus instanceof Wet) {
                Entity owner = this.owner;
                owner.removeStatus(this);
                owner.addStatus(new UnLit());
                Display.log("The " + owner.getName() + " goes out.", owner.getSpace());
            }
            return true;
        }

        @Override
        public boolean validateSamenessOut(Status status) {
            return status instanceof Wet || status instanceof Burning;
        }
        
    }

    private static class UnLit extends Status implements SeperateOut{

        public UnLit() {
            super();
            this.setCharacter(' ');
            this.setfGColor(TileColor.transparent());
            this.setbGColor(TileColor.transparent());
            this.setDescriptor("");
        }

        @Override
        public boolean onStackOut(Status sameStatus) {
            if (sameStatus instanceof Burning) {
                Entity owner = this.owner;
                owner.removeStatus(this);
                owner.addStatus(new Lit());
            }
            return true;
        }

        @Override
        public boolean validateSamenessOut(Status status) {
            return status instanceof Burning;
        }

        @Override
        public Tile getTile(double percent) {
            return Tile.empty();
        }
        
    }

}
