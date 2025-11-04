package game.gameobjects.terrains.gasses;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Flammable;
import game.gamelogic.IsDeterrent;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;
import game.gameobjects.terrains.liquids.Liquid;

public class Miasma extends Gas implements Flammable, IsDeterrent{

    public Miasma(int initialDensity){
        super(initialDensity);
        setCharacter(' ');
        setName("Miasma");
        setDescription("The stench of rot and decay permiates the air.");
        setCharacter(' ');
        setFgColor(TileColor.transparent());
        setBgColor(TileColor.create(242, 0, 255, 255));
        setSpreadFactor(0.7f);
    }

    @Override
    public int behave() {
        int t = super.behave();
        if (getSpace() != null && getSpace().isOccupied()){
            getSpace().getOccupant().dealDamage(randomNumber(0, getAmount()), DamageType.SUFFICATION);
        }
        return t;
    }

    @Override
    public int getFuelValue() {
        return getAmount();
    }

    @Override
    public void onBurn() {
        this.getSpace().removeTerrain(this);
    }

    @Override
    public Gas createSelf(int density) {
        return new Miasma(density);
    }

    @Override
    public Liquid getCondensationLiquid(int depth) {
        throw new UnsupportedOperationException("Unimplemented method 'getCondensationLiquid'");
    }

    @Override
    public boolean condenses() {
        return false;
    }

    @Override
    public double getDeterrent(Entity entity) {
        return entity.doResistancesAndVulns(100, DamageType.SUFFICATION)/10d;
    }
    
}

