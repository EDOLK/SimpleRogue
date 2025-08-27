package game.floorgeneration;

import static game.App.getRandom;

import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.gamelogic.HasInventory;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.Staircase;
import game.gameobjects.terrains.Trapdoor;
import kotlin.Pair;

public class BossFloorGenerator extends DefaultFloorGenerator {

    public BossFloorGenerator(int depth) {
        super(depth);
    }

    @Override
    protected int getRoomNumber(int depth) {
        return 3;
    }

    @Override
    protected void spawnEntities(Pair<List<Room>, List<Space[]>> pair) {
        List<Room> rooms = pair.getFirst();
        getRandom(rooms.get(0).getInteriorSpaces()).setOccupant(player);
        Item key = new Item(TileColor.transparent(),TileColor.create(219,186,53,255),'k');
        key.setName("Key");
        key.setWeight(1);
        key.setDescription("An ominous-looking key.");
        getRandom(rooms.get(2).getInteriorSpaces()).addTerrain(new Trapdoor(new Staircase(), key));
        Entity boss = Dungeon.getCurrentBossPool().getRandom(5*depth).get().get();
        if (boss instanceof HasInventory hasInventory) {
            hasInventory.addItemToInventory(key);
        } else {
            getRandom(rooms.get(1).getInteriorSpaces()).addItem(key);
        }
        getRandom(rooms.get(1).getInteriorSpaces()).setOccupant(boss);
    }

}
