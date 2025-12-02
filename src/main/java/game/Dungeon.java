package game;

import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.display.FloorMenu;
import game.floorgeneration.BossFloorGenerator;
import game.floorgeneration.DebugFloorGenerator;
import game.floorgeneration.DefaultFloorGenerator;
import game.floorgeneration.pools.LayerPool;
import game.floorgeneration.pools.Pool;
import game.floorgeneration.pools.layers.LayerOnePool;
import game.gamelogic.skilltrees.SkillTree;
import game.gamelogic.skilltrees.rogue.RogueSkillTree;
import game.gamelogic.skilltrees.warrior.WarriorSkillTree;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.entities.Wall;
import game.gameobjects.entities.props.Chest;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.Staircase;

public class Dungeon {

    private static Floor currentFloor;

    private static int currentDepth;

    private static int sX;

    private static int sY;
    
    private static LayerPool currentLayerPool = new LayerOnePool();

    private static List<SkillTree> availableSkillTrees = new ArrayList<>();

    public static int floorsWithoutMimic;

    public static List<SkillTree> getAvailableSkillTrees() {
        return availableSkillTrees;
    }

    public static Pool<Entity> getCurrentBossPool() {
        return currentLayerPool.BOSS_POOL.copy();
    }

    public static Pool<Chest> getCurrentChestPool() {
        return currentLayerPool.CHEST_POOL.copy();
    }

    public static Pool<Entity> getCurrentMonsterPool() {
        return currentLayerPool.MONSTER_POOL.copy();
    }

    public static Pool<Item> getCurrentDropPool() {
        return currentLayerPool.DROP_POOL.copy();
    }

    public static Pool<Item> getCurrentTreasurePool() {
        return currentLayerPool.TREASURE_POOL.copy();
    }

    public static Pool<Entity> getCurrentPropPool() {
        return currentLayerPool.PROP_POOL.copy();
    }

    public static Pool<Entity> getCurrentLonePropPool() {
        return currentLayerPool.LONE_PROP_POOL.copy();
    }

    public static Pool<Item> getCurrentPropDropPool() {
        return currentLayerPool.PROP_DROP_POOL.copy();
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
        currentFloor = generateFloor();
        availableSkillTrees.clear();
        availableSkillTrees.add(new WarriorSkillTree());
        availableSkillTrees.add(new RogueSkillTree());
        floorsWithoutMimic = 1;
    }

    public static void update(int time){
        currentFloor.update(time);
    }
    
    public static void update(){
        currentFloor.update();
    }

    public static void goDownFloor(Staircase staircase){
        PlayerEntity playerEntity = currentFloor.getPlayer();
        if (playerEntity.isAdjacent(staircase)){
            currentDepth++;
            if (currentDepth > 5 && currentDepth <= 10) {
                //currentLayerPool = new LayerTwoPool();
            } else if (currentDepth > 10 && currentDepth <= 15){
                //this.currentLayerPool = new LayerThreePool();
            } else if (currentDepth > 15 && currentDepth <= 20){
                //this.currentLayerPool = new LayerFourPool();
            }
            currentFloor = generateFloor(playerEntity);
            FloorMenu floorMenu = new FloorMenu();
            Display.setRootMenu(floorMenu);
            Display.replaceMenu(floorMenu);
            floorMenu.update();
        }
    }

    public static Floor generateFloor() {
        return generateFloor(new PlayerEntity(TileColor.transparent(), TileColor.create(255, 255, 255, 255), '@'));
    }
    
    public static Floor generateFloor(PlayerEntity playerEntity){
        if (currentDepth % 5 == 0) {
            return new Floor(sX, sY, playerEntity, new BossFloorGenerator(currentDepth));
        } else {
            return new Floor(sX, sY, playerEntity, new DefaultFloorGenerator(currentDepth));
        }
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

