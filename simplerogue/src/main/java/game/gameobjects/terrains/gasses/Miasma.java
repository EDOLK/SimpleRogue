package game.gameobjects.terrains.gasses;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Flammable;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.terrains.Fire;
import game.gameobjects.terrains.liquids.Liquid;

public class Miasma extends Gas implements Flammable{

    protected Space currentSpace;
    
    public Miasma(int initialDensity){
        super(initialDensity);
        setCharacter(' ');
        setName("Miasma");
        setDescription("The stench of rot and decay permiates the air.");
        setCharacter(' ');
        setfGColor(TileColor.transparent());
        setbGColor(TileColor.create(99, 0, 153, 255));
        this.minOpacity = 100;
        this.maxOpacity = 255;
    }


    @Override
    public void behave() {
        if (getSpace().isOccupied()){
            getSpace().getOccupant().dealDamage(randomNumber(0,getDensity()), DamageType.SUFFICATION);
        }
        super.behave();
    }


    @Override
    public Space getSpace() {
        return currentSpace;
    }


    @Override
    public void setSpace(Space space) {
        currentSpace = space;
    }


    @Override
    public int getFuelValue() {
        return getDensity();
    }


    @Override
    public void onBurn(Fire fire) {
        getSpace().getTerrains().remove(this);
    }

    @Override
    public Gas createSelf(int density) {
        return new Miasma(1);
    }


    @Override
    public Liquid getCondensationLiquid(int depth) {
        return null;
    }


    @Override
    public boolean condenses() {
        return false;
    }
    
    
    
}

