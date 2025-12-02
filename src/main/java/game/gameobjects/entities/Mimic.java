package game.gameobjects.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Tile;

import game.App;
import game.display.Display;
import game.gamelogic.DropsXP;
import game.gamelogic.HasInventory;
import game.gamelogic.Interactable;
import game.gamelogic.behavior.AnimalHunting;
import game.gamelogic.combat.Attack;
import game.gameobjects.DamageType;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.PseudoStatus;
import game.gameobjects.statuses.Sleeping;
import game.gameobjects.statuses.Status;

public class Mimic extends Animal implements Interactable, DropsXP, HasInventory{

    private boolean activated;
    private int activeTimer;
    private Entity disguise;
    private List<Item> inventory = new ArrayList<>();

    @Override
    public List<Status> getStatuses() {
        if (!activated) {
            return Stream.concat(
                super.getStatuses().stream(),
                disguise.getStatuses().stream().map(s -> new PseudoStatus(s))
            ).collect(Collectors.toList());
        }
        return super.getStatuses();
    }

    @Override
    public Tile getTile(double percent) {
        if (!activated)
            return disguise.getTile(percent);
        return super.getTile(percent);
    }

    @Override
    public String getOriginalName() {
        if (!activated)
            return disguise.getOriginalName();
        return super.getOriginalName();
    }

    @Override
    public String getDescription() {
        if (!activated)
            return disguise.getDescription();
        return "It looks like a " + disguise.getOriginalName() + ", but wrong...";
    }

    @Override
    public boolean isLiquidBlocker() {
        if (!activated)
            return disguise.isLiquidBlocker();
        return super.isLiquidBlocker();
    }

    @Override
    public boolean isLightBlocker() {
        if (!activated)
            return disguise.isLightBlocker();
        return super.isLightBlocker();
    }

    @Override
    public boolean isGasBlocker() {
        if (!activated)
            return disguise.isGasBlocker();
        return super.isGasBlocker();
    }

    @Override
    public boolean isSightBlocker(){
        if (!activated)
            return disguise.isSightBlocker();
        return super.isSightBlocker();
    }

    @Override
    public boolean isItemBlocker(){
        if (!activated)
            return disguise.isItemBlocker();
        return super.isItemBlocker();
    }

    public Mimic(Entity disguise){
        super(TileColor.transparent(), TileColor.create(150, 10, 150, 255), 'M');
        this.disguise = disguise;
        setName("Mimic");
        Weapon mass = new Weapon();
        mass.setDamage(1, 6);
        mass.setDamageType(DamageType.BLUNT);
        setUnarmedWeapon(mass);
        setBaseMaxHP(10);
        setHP(10);
        setWeight(5);
        new ArrayList<>(super.getStatuses()).stream().filter(s -> s instanceof Sleeping).forEach(this::removeStatus);
        if (disguise instanceof HasInventory hi)
            inventory.addAll(hi.getInventory());
    }

    @Override
    public int behave() {
        if (!(getBehavior() instanceof AnimalHunting)) {
            if (activeTimer > 0) {
                activeTimer--;
            } else {
                deactivate();
                return this.getTimeToWait();
            }
        }
        return super.behave();
    }

    @Override
    public boolean isActive() {
        return super.isActive() && activated;
    }

    @Override
    public int defaultInteraction(Entity interactor) {
        if (!activated) {
            activate(interactor);
            return interactor.getTimeToWait();
        }
        return super.defaultInteraction(interactor);
    }

    private void activate(Entity activator){
        Display.log("The " + getName() + " is a mimic!", getSpace());
        activated = true;
        activeTimer = 15;
        if (this.isEnemy(activator)) {
            try {
                setBehavior(new AnimalHunting(this, activator));
            } catch (Exception e) {
            }
        }
    }

    private void deactivate(){
        boolean t = false;
        if (Math.random() < 0.25){
            List<Entity> entitiesInVision = getEntitiesInVision();
            if (entitiesInVision != null && !entitiesInVision.isEmpty()) {
                this.disguise = App.getRandom(entitiesInVision);
                t = true;
            }
        }
        Display.log("The " + getName() + " transforms " + (t ? "" : "back ") + "into a " + this.disguise.getName() + ".", getSpace());
        activated = false;
    }

    @Override
    public void modifyAttack(Attack attack) {
        if (attack.getDefender() == this && !activated)
            activate(attack.getAttacker());
        super.modifyAttack(attack);
    }

    @Override
    public void onInteract(Entity interactor) {
        if (!activated)
            activate(interactor);
    }

    @Override
    public int dropXP() {
        return 10;
    }

    @Override
    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public int getHardWeightLimit() {
        return 999;
    }

}
