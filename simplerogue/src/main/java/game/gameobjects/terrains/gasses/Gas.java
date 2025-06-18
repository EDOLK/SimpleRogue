package game.gameobjects.terrains.gasses;

import game.gamelogic.Examinable;
import game.gameobjects.Space;
import game.gameobjects.terrains.SpreadableTerrain;
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
    public int getMinSpreadAmount() {
        return 1;
    }

    @Override
    public String getDescription(){
        return this.description;
    }

    @Override
    public String getName() {
        return this.name;
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
