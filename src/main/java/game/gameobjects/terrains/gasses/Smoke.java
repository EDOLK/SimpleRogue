package game.gameobjects.terrains.gasses;

import org.hexworks.zircon.api.color.TileColor;

import game.gameobjects.terrains.SpreadableTerrain;
import game.gameobjects.terrains.liquids.Liquid;

public class Smoke extends Gas {

    public Smoke(int amount) {
        super(amount);
        setCharacter(' ');
        setName("Smoke");
        setDescription("This is smoke.");
        setCharacter(' ');
        setFgColor(TileColor.transparent());
        setBgColor(TileColor.create(100, 100, 100, 255));
        setSpreadFactor(0.3f);
        setDisapparationRate(15);
    }

    @Override
    public boolean isSightBlocker() {
        return this.amount >= 3;
    }

    @Override
    public int getLightAbsorption() {
        return this.amount / 2;
    }

    @Override
    public boolean condenses() {
        return false;
    }

    @Override
    public Liquid getCondensationLiquid(int amount) {
        throw new UnsupportedOperationException("Unimplemented method 'getCondensationLiquid'");
    }

    @Override
    protected SpreadableTerrain createSelf(int amount) {
        return new Smoke(amount);
    }
    
}
