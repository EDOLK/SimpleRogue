package game.gameobjects.items;

import java.util.Collection;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.display.Display;
import game.gamelogic.Flammable;
import game.gamelogic.LightSource;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.Attack;
import game.gamelogic.combat.AttackModifier;
import game.gamelogic.floorinteraction.SelectionResult;
import game.gamelogic.floorinteraction.SimpleSelector;
import game.gamelogic.interactions.HasInteractions;
import game.gamelogic.interactions.Interaction;
import game.gamelogic.interactions.InteractionResult;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Burning;
import game.gameobjects.terrains.Fire;

public class Torch extends Weapon implements Flammable, LightSource, SelfAware, Behavable, AttackModifier, HasInteractions{
    
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
            decrementFuel();
        }
        return 100;
    }

    private void decrementFuel(){
        decrementFuel(1);
    }

    private void decrementFuel(int amount){
        this.fuel -= amount;
        if (this.fuel <= 0) {
            setBurnt();
        }
    }

    @Override
    public boolean isActive() {
        return lit;
    }

    @Override
    public Collection<Interaction> getInteractions() {
        return List.of(
            new Interaction.Builder()
                .withName("Light")
                .withOnInteract((interactor) -> {
                    if (!lit){
                        setLit();
                    }
                    return InteractionResult.create()
                        .withTimeTaken(0)
                        .withRevertMenu();
                })
                .withIsDisabled((interactor) -> {
                    return lit || fuel <= 0;
                })
                .build(),
            new Interaction.Builder()
                .withName("Kindle")
                .withOnInteract((interactor) -> {
                    Display.getRootMenu().startSelecting(new KindleSelector());
                    return InteractionResult.create()
                        .withTimeTaken(0)
                        .withRevertMenu();
                })
                .withIsDisabled((interactor) -> {
                    return !lit;
                })
                .build()
        );
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
    public boolean canStack(Item otherItem) {
        if (otherItem instanceof Torch torch) {
            return this.lit == torch.lit && this.fuel == torch.fuel;
        }
        return super.canStack(otherItem);
    }

    @Override
    public void modifyAttack(Attack attack) {
        attack.attachPostAttackHook((attackResult) -> {
            if (attackResult.weapon() == this && attackResult.hit() && attackResult.crit() && lit)
                attackResult.defender().addStatus(new Burning());
        });
    }

    private class KindleSelector implements SimpleSelector {

        @Override
        public SelectionResult simpleSelect(Space space) {
            if (Torch.this.lit && Fire.isFlammable(space)) {
                Torch.this.decrementFuel(100);
                space.addFire(new Fire(1));
                return new SelectionResult(true, 100);
            }
            return new SelectionResult(true, 0);
        }

    }

}
