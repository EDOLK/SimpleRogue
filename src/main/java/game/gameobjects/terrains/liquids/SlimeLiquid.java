package game.gameobjects.terrains.liquids;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.statuses.Slimed;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.SpreadableTerrain;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.gasses.Gas;

public class SlimeLiquid extends Liquid {

    public SlimeLiquid(int amount) {
        super(amount);
        setCharacter(' ');
        setFgColor(TileColor.transparent());
        setBgColor(TileColor.create(50, 255, 50, 255));
        setName("Slime");
        // setTileName("Water");
        setSpreadFactor(0.20f);
        setDisapparationRate(10);
    }

    @Override
    public boolean evaporates() {
        return false;
    }

    @Override
    public Gas getEvaporationGas(int amount) {
        throw new UnsupportedOperationException("Unimplemented method 'getEvaporationGas'");
    }

    @Override
    public boolean freezes() {
        return false;
    }

    @Override
    public Terrain getFreezeTerrain(int amount) {
        throw new UnsupportedOperationException("Unimplemented method 'getFreezeTerrain'");
    }

    @Override
    public boolean wets() {
        return true;
    }

    @Override
    protected SpreadableTerrain createSelf(int amount) {
        return new SlimeLiquid(amount);
    }

    @Override
    public Status getWetStatus(int amount) {
        return new Slimed(this);
    }

}
