package game.gamelogic.floorinteraction;

import game.display.Cursor;
import game.gamelogic.HasInventory;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.projectiles.ThrownItem;

public class AimSelector implements Selector {

    private Item item;
    private Entity thrower;

    public AimSelector(Item item, Entity thrower) {
        this.item = item;
        this.thrower = thrower;
    }

    @Override
    public SelectionResult select(Cursor cursor) {
        Space aimingSpace = cursor.getSelectedSpace();
        ThrownItem thrownItem = ThrownItem.throwItem(thrower, aimingSpace, item);
        if (thrownItem != null && thrower instanceof HasInventory hi && hi.getInventory().contains(item)) {
            hi.removeItemFromInventory(item);
            thrower.getSpace().addTerrain(thrownItem);
        }
        //TODO: different throwing times based on item weight, thrower strength, etc.
        return new SelectionResult(true, 100);
    }

    @Override
    public boolean canMove(Cursor cursor, Space toSpace) {
        return true;
    }

}
