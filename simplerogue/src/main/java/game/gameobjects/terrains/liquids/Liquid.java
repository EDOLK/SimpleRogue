package game.gameobjects.terrains.liquids;

import game.gamelogic.Examinable;
import game.gameobjects.Space;
import game.gameobjects.terrains.SpreadableTerrain;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.gasses.Gas;

public abstract class Liquid extends SpreadableTerrain implements Examinable {

    private String description;

    private String name;

    public abstract boolean evaporates();

    public abstract Gas getEvaporationGas(int amount);

    public abstract boolean freezes();

    public abstract Terrain getFreezeTerrain(int amount);

    public Liquid(int amount) {
        super(amount);
        setDisapparationRate(100);
    }

    @Override
    public int getMinSpreadAmount() {
        return 2;
    }

    @Override
    public String getDescription(){
        return this.description;
    }

    @Override
    public String getName(){
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected boolean isValidToSpread(Space potentialSpace) {
        return !potentialSpace.isOccupied() || !potentialSpace.getOccupant().isLiquidBlocker();
    }


}
