package game.gameobjects.items;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.display.Display;
import game.gamelogic.Flammable;
import game.gamelogic.Interactable;
import game.gamelogic.LightSource;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.terrains.Fire;

public class Torch extends Item implements Flammable, LightSource, SelfAware, Behavable, Interactable {

    private boolean lit = false;
    private int fuel = 100;
    private Space space;

    public Torch(boolean lit) {
        super();
        this.lit = lit;
        setName("Torch");
        setCharacter('t');
        setfGColor(TileColor.create(250, 134, 7, 255));
        setbGColor(TileColor.transparent());
        setDescription("A torch.");
        setWeight(1);
    }

    public Torch() {
        this(false);
    }

    @Override
    public String getName() {
        return super.getName() + (lit ? " (lit)" : "");
    }

    @Override
    public int getLightSourceIntensity() {
        if (lit){
            return (int)App.lerp(0,3,100,15,fuel);
        } else {
            return 0;
        }
    }

    @Override
    public int getFuelValue() {
        return fuel/10;
    }

    @Override
    public void onBurn(Fire fire) {
        space.remove(this);
    }

    @Override
    public Space getSpace() {
        return this.space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public void behave() {
        fuel--;
    }

    @Override
    public boolean isActive() {
        return lit;
    }

    @Override
    public void onInteract(Entity interactor) {
        if (!lit){
            lit = true;
        } else {
            Display.log("The torch is already lit.");
        }
        Display.revertMenu();
    }

}
