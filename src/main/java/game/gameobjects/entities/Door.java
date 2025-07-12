package game.gameobjects.entities;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.HasResistances;
import game.gamelogic.Interactable;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.OpenDoor;

public class Door extends Entity implements Interactable, HasResistances{
    
    private ArrayList<Resistance> resistances = new ArrayList<Resistance>();
    
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

        resistances.add(new PercentageResistance(DamageType.BLEED, 1.0));
        resistances.add(new PercentageResistance(DamageType.FROST, 1.0));
        resistances.add(new PercentageResistance(DamageType.POISON, 1.0));
        resistances.add(new PercentageResistance(DamageType.SUFFICATION, 1.0));
        
    }

    @Override
    public Item getCorpse() {
        Item i = new Item(this.getbGColor().darkenByPercent(.50), this.getfGColor().darkenByPercent(.50), '░');
        i.setName("Broken Door");
        i.setDescription("A broken door.");
        i.setWeight(getBaseWeight());
        return i;
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
        return resistances;
    }

    @Override
    public String getDeathMessage() {
        return "The " + getName() + " breaks.";
    }



    
}
