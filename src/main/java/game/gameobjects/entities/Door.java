package game.gameobjects.entities;

import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Flammable;
import game.gamelogic.HasResistances;
import game.gamelogic.Interactable;
import game.gamelogic.SelfAware;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Burning;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.OpenDoor;

public class Door extends Entity implements Interactable, HasResistances{
    
    public Door(Character c){
        super(TileColor.transparent(), TileColor.create(181, 88, 45, 255), c);
        setBaseMaxHP(10);
        setHP(10);
        setName("Door");
        setTileName("Closed Door");
        setDescription("A door.");
        setWeight(10);
        setSightBlocker(true);
        setGasBlocker(true);
        setLiquidBlocker(true);
        setLightBlocker(true);
    }

    @Override
    public Item getCorpse() {
        return new BrokenDoor();
    }

    private static class BrokenDoor extends Item implements Flammable, SelfAware {

        private Space space;

        public BrokenDoor() {
            super(TileColor.transparent(), TileColor.create(181, 88, 45, 255), '}');
            setName("Broken Door");
            setDescription("A broken door.");
            setWeight(10);
        }

        @Override
        public int getFuelValue() {
            return 3;
        }

        @Override
        public void onBurn() {
            this.space.removeItem(this);
        }

        @Override
        public Space getSpace() {
            return this.space;
        }

        @Override
        public void setSpace(Space space) {
            this.space = space;
        }

    }

    @Override
    public void onInteract(Entity interactor) {
        getSpace().setOccupant(null);
        getSpace().addTerrain(new OpenDoor(this));
    }

    @Override
    public int defaultInteraction(Entity interactor) {
        onInteract(interactor);
        return interactor.getTimeToMove();
    }

    @Override
    public List<Resistance> getResistances() {
        return List.of(
            new PercentageResistance(DamageType.FROST, 1.0),
            new PercentageResistance(DamageType.POISON, 1.0),
            new PercentageResistance(DamageType.BLEED, 1.0),
            new PercentageResistance(DamageType.SUFFICATION, 1.0)
        );
    }

    @Override
    public String getDeathMessage() {
        return "The " + getName() + " breaks.";
    }

    @Override
    protected boolean baseVulnerable(Status status) {
        return status instanceof Burning;
    }
    
}
