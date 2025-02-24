package game.gameobjects.items;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.display.Display;
import game.display.ItemContextMenu;
import game.gamelogic.Flammable;
import game.gamelogic.Interactable;
import game.gamelogic.LightSource;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnCrit;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Burning;
import game.gameobjects.terrains.Fire;

public class Torch extends Weapon implements Flammable, LightSource, SelfAware, Behavable, Interactable, OnCrit{
    
    private boolean lit = false;
    private int maxFuel = 500;
    private int fuel = maxFuel;
    private Space space;

    public Torch(boolean lit) {
        super();
        this.lit = lit;
        setName("Torch");
        setCharacter('t');
        setfGColor(TileColor.create(250, 134, 7, 255));
        setbGColor(TileColor.transparent());
        setDescription("A torch. Can light nearby tiles on fire, assuming there is kindling, of course.");
        setWeight(1);
        if (lit) {
            setMinDamage(0);
            setMaxDamage(3);
            setDamageType(DamageType.FIRE);
        } else {
            setMinDamage(0);
            setMaxDamage(2);
            setDamageType(DamageType.BLUNT);
        }
    }

    public Torch() {
        this(false);
    }

    @Override
    public String getName() {
        return super.getName() + (lit ? " (" + fuel + ")" : "");
    }

    @Override
    public int getLightSourceIntensity() {
        if (lit){
            return (int)App.lerp(0,3,maxFuel,11,fuel);
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
        if (lit) {
            fuel--;
        }
        if (fuel <= 0) {
            lit = false;
            this.setName("Burnt Torch");
            setMinDamage(0);
            setMaxDamage(2);
            setDamageType(DamageType.BLUNT);
            Display.log("Your torch goes out.");
        }
    }

    @Override
    public boolean isActive() {
        return lit;
    }

    @Override
    public void onInteract(Entity interactor) {
        if (!lit){
            lit = true;
            setMinDamage(0);
            setMaxDamage(3);
            setDamageType(DamageType.FIRE);
        } else {
            Display.log("The torch is already lit.");
        }
        if (Display.getCurrentMenu() instanceof ItemContextMenu)
            Display.revertMenu();
    }

    @Override
    public void doOnCrit(Entity self, Entity other, AttackInfo attackInfo) {
        if (lit)
            other.addStatus(new Burning());
    }

}
