package game;

import static game.App.randomNumber;

import game.display.Display;
import game.display.FloorMenu;
import game.floorgeneration.DebugFloorGenerator;
import game.floorgeneration.DefaultFloorGenerator;
import game.floorgeneration.Pool;
import game.floorgeneration.Pools;
import game.floorgeneration.ItemFactory.ItemIdentifier;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.entities.Wall;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.Staircase;

public class Dungeon {

    private static Floor currentFloor;

    private static int currentDepth;
    
    private static Pool<Class<? extends Entity>> currentMonsterPool = Pools.LAYER_ONE_MONSTER_POOL;
    
    private static Pool<ItemIdentifier> currentDropPool = Pools.LAYER_ONE_DROP_POOL;
    
    private static Pool<ItemIdentifier> currentTreasurePool = Pools.LAYER_ONE_TREASURE_POOL;
    
    private static Pool<Class<? extends Entity>> currentChestPool = Pools.LAYER_ONE_CHEST_POOL;

    public static Pool<Class<? extends Entity>> getCurrentChestPool() {
        return currentChestPool;
    }

    public static Pool<Class<? extends Entity>> getCurrentMonsterPool() {
        return currentMonsterPool;
    }

    public static Pool<ItemIdentifier> getCurrentDropPool() {
        return currentDropPool;
    }

    public static Pool<ItemIdentifier> getCurrentTreasurePool() {
        return currentTreasurePool;
    }

    public static void setCurrentFloor(Floor currentFloor) {
        Dungeon.currentFloor = currentFloor;
    }

    public static void setCurrentDepth(int currentDepth) {
        Dungeon.currentDepth = currentDepth;
    }

    public static Floor getCurrentFloor(){
        return currentFloor;
    };
    
    public static int getCurrentDepth(){
        return currentDepth;
    }
    
    public static void initialize(int sizeX, int sizeY){
        currentDepth = 1;
        currentFloor = new Floor(sizeX, sizeY, new DefaultFloorGenerator(currentDepth));
    }
    
    public static void update(){
        currentFloor.update();
    }

    public static void goDownFloor(Staircase staircase){
        PlayerEntity playerEntity = currentFloor.getPlayer();
        if (playerEntity.isAdjacent(staircase)){
            currentDepth++;
            currentFloor = generateFloor(currentDepth, playerEntity);
            FloorMenu floorMenu = new FloorMenu();
            Display.setRootMenu(floorMenu);
            Display.setMenu(floorMenu);
            floorMenu.update();
        }
    }
    
    public static Floor generateFloor(int depth, PlayerEntity playerEntity){
        return new Floor(50, 50, playerEntity, new DefaultFloorGenerator(currentDepth));
    }
    
    public static void addItem(Item item, int x, int y){
        currentFloor.getSpace(x, y).addItem(item);
    }

    public static void spawnItem(Item item){
        int x = randomNumber(0, currentFloor.SIZE_X-1);
        int y = randomNumber(0, currentFloor.SIZE_Y-1);
        if (currentFloor.getSpace(x, y).isOccupied() && currentFloor.getSpace(x, y).getOccupant() instanceof Wall){
            x = randomNumber(0, currentFloor.SIZE_X-1);
            y = randomNumber(0, currentFloor.SIZE_Y-1);
        }
        currentFloor.getSpace(x, y).addItem(item);
    }
    
    public static boolean addEntity(Entity entity, int x, int y){
        Space space = currentFloor.getSpace(x, y);
        if (!space.isOccupied()){
            space.setOccupant(entity);
            return true;
        }
        return false;
    }
    
    public static boolean spawnEntity(Entity entity){
        int x = randomNumber(0, currentFloor.SIZE_X-1);
        int y = randomNumber(0, currentFloor.SIZE_Y-1);
        int tries = 0;
        if (currentFloor.getSpace(x, y).isOccupied()){
            if (tries > 100)
                return false;
            x = randomNumber(0, currentFloor.SIZE_X-1);
            y = randomNumber(0, currentFloor.SIZE_Y-1);
            tries++;
        }
        return addEntity(entity, x, y);
    }

}

