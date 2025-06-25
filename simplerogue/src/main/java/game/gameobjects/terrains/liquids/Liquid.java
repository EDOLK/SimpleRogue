package game.gameobjects.terrains.liquids;

import java.util.ArrayList;
import java.util.List;

import game.gamelogic.Examinable;
import game.gameobjects.Space;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.Fire;
import game.gameobjects.terrains.SpreadableTerrain;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.gasses.FreezingAir;
import game.gameobjects.terrains.gasses.Gas;

public abstract class Liquid extends SpreadableTerrain implements Examinable {

    private String description;

    private String name;

    public abstract boolean evaporates();

    public abstract Gas getEvaporationGas(int amount);

    public abstract boolean freezes();

    public abstract Terrain getFreezeTerrain(int amount);

    public abstract boolean wets();

    public abstract Status getWetStatus(int amount);

    @Override
    public int behave() {
        if (this.getSpace().isOccupied() && this.wets()) {
            this.getSpace().getOccupant().addStatus(this.getWetStatus(1));
        }
        List<Terrain> terrainsToAdd = new ArrayList<>();
        if (this.evaporates() || this.freezes()) {
            for (Terrain terrain : this.getSpace().getTerrains()) {
                switch (terrain) {
                    case Fire fire -> {
                        int a = Math.min(this.getAmount(), fire.getFuel());
                        this.setAmount(this.getAmount() - a);
                        fire.subtractFuel(a);
                        terrainsToAdd.add(this.getEvaporationGas(a));
                    }
                    case FreezingAir fAir -> {
                        int a = Math.min(this.getAmount(), fAir.getAmount());
                        this.setAmount(this.getAmount() - a);
                        fAir.setAmount(fAir.getAmount() - a);
                        terrainsToAdd.add(this.getFreezeTerrain(a));
                    }
                    default -> {

                    }
                }
                if (this.getAmount() <= 0) {
                    break;
                }
            }
        }
        terrainsToAdd.forEach(getSpace()::addTerrain);
        return super.behave();
    }

    public Liquid(int amount) {
        super(amount);
        setDisapparationRate(50);
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
        return this.name + " (" + this.amount + ")";
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
