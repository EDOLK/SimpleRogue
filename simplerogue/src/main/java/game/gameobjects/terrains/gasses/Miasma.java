package game.gameobjects.terrains.gasses;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Flammable;
import game.gameobjects.DamageType;
import game.gameobjects.terrains.liquids.Liquid;

public class Miasma extends Gas implements Flammable{

    public Miasma(int initialDensity){
        super(initialDensity);
        setCharacter(' ');
        setName("Miasma");
        setDescription("The stench of rot and decay permiates the air.");
        setCharacter(' ');
        setfGColor(TileColor.transparent());
        setbGColor(TileColor.create(99, 0, 153, 255));
        setSpreadFactor(0.9f);
    }

    @Override
    public int behave() {
        if (getSpace().isOccupied()){
            getSpace().getOccupant().dealDamage(randomNumber(0, getAmount()), DamageType.SUFFICATION);
        }
        return super.behave();
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
    
}

