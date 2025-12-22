package game.gamelogic.skilltrees.rogue;


import java.util.List;
import java.util.function.BiConsumer;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.Dungeon;
import game.display.Display;
import game.gamelogic.Aimable;
import game.gamelogic.HasInventory;
import game.gamelogic.Levelable;
import game.gamelogic.abilities.Ability;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.floors.Floor;
import game.gameobjects.ItemSlot;
import game.gameobjects.Space;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.gasses.Smoke;

public class SmokeBombAbility implements Ability, Levelable, Behavable{

    private int cooldown;
    private int level = 1;
    private int amount = 0;
    private HasInventory owner;

    public SmokeBombAbility(HasInventory owner) {
        this.owner = owner;
    }

    @Override
    public String getName() {
        return "Craft Smoke Bomb [" + amount + "/" + level + "]" + (cooldown > 0 ? " (" + cooldown + ")" : "");
    }

    @Override
    public void activate() {
        if (amount < level && owner.addItemToInventory(new SmokeBomb())) {
            amount++;
        }
        cooldown = 100;
        Display.revertMenu();
    }

    @Override
    public boolean isEnabled() {
        return cooldown <= 0 && amount < level;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public boolean setLevel(int level) {
        this.level = level;
        return true;
    }

    @Override
    public int behave() {
        cooldown--;
        return 100;
    }

    @Override
    public boolean isActive() {
        return cooldown > 0;
    }


    public class SmokeBomb extends Item implements Aimable {

        private BiConsumer<Floor, Floor> hook = (f1, f2) -> {
            PlayerEntity player = f2.getPlayer();
            List<Item> inventory = player.getInventory();
            ItemSlot slot = player.getOffHandSlot();
            if (!inventory.contains(this) && (slot.getEquippedItem() == null || slot.getEquippedItem() != this)){
                amount--;
            }
        };

        public SmokeBomb() {
            super(TileColor.transparent(), TileColor.create(100, 100, 100, 255), 'o');
            setName("Smoke Bomb");
            setWeight(1);
            setDescription("A small, egg-shaped gizmo. It smells vaguely smokey.");
            Dungeon.getCurrentFloor().attachHook(hook);
        }

        @Override
        public boolean collides(Space space) {
            return space.isOccupied();
        }

        @Override
        public void onCollision(Space beforeSpace, Space collidingSpace) {
            if (!collidingSpace.getOccupant().isGasBlocker()) {
                doSmoke(collidingSpace);
            } else if (!beforeSpace.isOccupied() || !beforeSpace.getOccupant().isGasBlocker()){
                doSmoke(beforeSpace);
            }
            amount--;
            Dungeon.getCurrentFloor().detachHook(hook);
        }

        @Override
        public void onLand(Space space) {
            doSmoke(space);
            amount--;
            Dungeon.getCurrentFloor().detachHook(hook);
        }

        private void doSmoke(Space space){
            switch (level) {
                case 2:
                    space.addTerrain(new Smoke(6));
                    Space.getAdjacentSpaces(space).stream()
                        .filter((s) -> !s.isOccupied() || !s.getOccupant().isGasBlocker())
                        .forEach((s) -> s.addTerrain(new Smoke(App.randomNumber(3, 6))));
                    break;
                case 3:
                    space.addTerrain(new Smoke(10));
                    Space.getAdjacentSpaces(space).stream()
                        .filter((s) -> !s.isOccupied() || !s.getOccupant().isGasBlocker())
                        .forEach((s) -> s.addTerrain(new Smoke(App.randomNumber(6, 10))));
                    break;
                default:
                    space.addTerrain(new Smoke(App.randomNumber(3, 6)));
                    break;
            }
        }


    }

}
