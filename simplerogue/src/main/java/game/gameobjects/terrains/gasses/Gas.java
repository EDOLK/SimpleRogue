package game.gameobjects.terrains.gasses;

import java.util.ArrayList;
import java.util.List;

import game.gamelogic.Examinable;
import game.gameobjects.Space;
import game.gameobjects.terrains.SpreadableTerrain;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.liquids.Liquid;

public abstract class Gas extends SpreadableTerrain implements Examinable {

    private String description;

    private String name;

    public abstract boolean condenses();

    public abstract Liquid getCondensationLiquid(int amount);

    public Gas(int amount) {
        super(amount);
        setDisapparationRate(50);
    }

    @Override
    public int behave() {
        List<Terrain> terrainsToAdd = new ArrayList<>();
        if (this.condenses()) {
            for (Terrain terrain : this.getSpace().getTerrains()) {

                if (terrain instanceof FreezingAir fAir) {
                    int a = Math.min(this.getAmount(), fAir.getAmount());
                    this.setAmount(this.getAmount() - a);
                    fAir.setAmount(fAir.getAmount() - a);
                    terrainsToAdd.add(this.getCondensationLiquid(a));
                }

                if (this.amount <= 0) {
                    break;
                }

            }
        }
        terrainsToAdd.forEach(getSpace()::addTerrain);
        return super.behave();
    }

    @Override
    public int getMinSpreadAmount() {
        return 1;
    }

    @Override
    public String getDescription(){
        return this.description;
    }

    @Override
    public String getName() {
        return this.name + " (" + this.amount + ")";
    };

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected boolean isValidToSpread(Space potentialSpace) {
        return !potentialSpace.isOccupied() || !potentialSpace.getOccupant().isGasBlocker();
    }


}
