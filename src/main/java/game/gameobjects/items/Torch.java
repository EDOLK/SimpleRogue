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
        setFgColor(TileColor.create(138, 64, 22, 255));
        setBgColor(TileColor.transparent());
        setDescription("A torch. Can light nearby tiles on fire, assuming there is kindling, of course.");
        setWeight(1);
        setMinDamage(0);
        setMaxDamage(2);
        setDamageType(DamageType.BLUNT);
        if (lit) {
            setLit();
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
    public void onBurn() {
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
    public int behave() {
        if (lit) {
            fuel--;
        }
        if (fuel <= 0) {
            setBurnt();
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return lit;
    }

    @Override
    public void onInteract(Entity interactor) {
        if (!lit){
            setLit();
        } else {
            Display.log("The torch is already lit.");
        }
        if (Display.getCurrentMenu() instanceof ItemContextMenu)
            Display.revertMenu();
    }

    private void setLit(){
        lit = true;
        setMinDamage(0);
        setMaxDamage(3);
        setDamageType(DamageType.FIRE);
        setFgColor(TileColor.create(227, 118, 16, 255));
    }

    private void setBurnt(){
        lit = false;
        Display.log("The " + getName() + " goes out.");
        setName("Burnt Torch");
        setMinDamage(0);
        setMaxDamage(2);
        setDamageType(DamageType.BLUNT);
        setDescription("A torch. This one's all spent.");
        setFgColor(TileColor.create(87, 57, 29, 255));
    }

    @Override
    public void doOnCrit(Entity self, Entity other, AttackInfo attackInfo) {
        if (lit)
            other.addStatus(new Burning());
    }

    @Override
    public boolean canStack(Item otherItem) {
        if (otherItem instanceof Torch torch) {
            return this.lit == torch.lit && this.fuel == torch.fuel;
        }
        return super.canStack(otherItem);
    }

}
