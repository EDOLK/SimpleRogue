package game;

import static game.App.randomNumber;

import java.util.function.Supplier;

import game.display.Display;
import game.display.FloorMenu;
import game.floorgeneration.DefaultFloorGenerator;
import game.floorgeneration.Pool;
import game.floorgeneration.Pools;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Chest;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.entities.Wall;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.Staircase;

public class Dungeon {

    private static Floor currentFloor;

    private static int currentDepth;

    private static int sX;

    private static int sY;
    
    private static Pool<Supplier<Entity>> currentMonsterPool = Pools.LAYER_ONE_MONSTER_POOL;
    
    private static Pool<Supplier<Item>> currentDropPool = Pools.LAYER_ONE_DROP_POOL;
    
    private static Pool<Supplier<Item>> currentTreasurePool = Pools.LAYER_ONE_TREASURE_POOL;
    
    private static Pool<Supplier<Chest>> currentChestPool = Pools.LAYER_ONE_CHEST_POOL;

    public static Pool<Supplier<Chest>> getCurrentChestPool() {
        return currentChestPool;
    }

    public static Pool<Supplier<Entity>> getCurrentMonsterPool() {
        return currentMonsterPool;
    }

    public static Pool<Supplier<Item>> getCurrentDropPool() {
        return currentDropPool;
    }

    public static Pool<Supplier<Item>> getCurrentTreasurePool() {
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
        sX = sizeX;
        sY = sizeY;
        currentFloor = new Floor(sX, sY, new DefaultFloorGenerator(currentDepth));
    }
    
    public static void update(){
        currentFloor.update();
    }

    public static void goDownFloor(Staircase staircase){
        PlayerEntity playerEntity = currentFloor.getPlayer();
        if (playerEntity.isAdjacent(staircase)){
            currentDepth++;
            currentFloor = generateFloor(playerEntity);
            FloorMenu floorMenu = new FloorMenu();
            Display.setRootMenu(floorMenu);
            Display.replaceMenu(floorMenu);
            floorMenu.update();
        }
    }
    
    public static Floor generateFloor(PlayerEntity playerEntity){
        return new Floor(sX, sY, playerEntity, new DefaultFloorGenerator(currentDepth));
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

