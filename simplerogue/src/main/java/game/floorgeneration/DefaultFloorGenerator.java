package game.floorgeneration;

import static game.App.randomNumber;

import java.util.function.Supplier;

import game.Dungeon;
import game.gameobjects.Space;
import game.gameobjects.entities.Chest;
import game.gameobjects.entities.Door;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.entities.Wall;
import game.gameobjects.terrains.Staircase;

public class DefaultFloorGenerator extends FloorGenerator{

    public DefaultFloorGenerator(int depth) {
        super(depth);
    }

    public void generateSpaces(Space[][] spaces){
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                spaces[x][y] = new Space(x, y);
            }
        }
    }

    public void generateWalls(Space[][] spaces) {
        int SIZE_X = spaces.length;
        int SIZE_Y = spaces[0].length;
        for (int x = 0; x < spaces.length; x++){
            for (int y = 0; y < spaces[x].length; y++){
                Wall wall = new Wall();
                spaces[x][y].setOccupant(wall);
                wall.setSpace(spaces[x][y]);
            }
        }

        int roomNumber = 25;
        int prevX1 = 0;
        int prevY1 = 0;
        int prevX2 = 0;
        int prevY2 = 0;
        int checks = 0;

        for (int i = 0; i < roomNumber; i ++){
            int roomWidth = randomNumber(3, 10);
            int roomHeight = randomNumber(3, 10);
            int x1 = randomNumber(1, SIZE_X-1-roomWidth);
            int y1 = randomNumber(1, SIZE_Y-1-roomHeight);
            int x2 = x1+roomWidth;
            int y2 = y1+roomHeight;

            boolean checkFailed = false;
            outer:
            for (int x = x1-1; x < x2+1; x++){
                for (int y = y1-1; y < y2+1; y++){
                    if (!spaces[x][y].isOccupied()){
                        checkFailed = true;
                        checks++;
                        break outer;
                    }
                }
            }
            if (checkFailed){
                if (checks < 1000){
                    i--;
                } else {
                    checks = 0;
                }
                continue;
            }
            for (int x = x1; x < x2; x++){
                for (int y = y1; y < y2; y++){
                    spaces[x][y].setOccupant(null);
                }
            }

            if (i != 0){

                int fromX = randomNumber(x1, x2-1);
                int fromY = randomNumber(y1, y2-1);

                int toX = randomNumber(prevX1, prevX2-1);
                int toY = randomNumber(prevY1, prevY2-1);

                boolean digging = false;
                if (randomNumber(0, 1) == 0){
                    fromX = digThroughX(spaces, fromX, fromY, toX, digging);
                    fromY = digThroughY(spaces, fromX, fromY, toY, digging);
                } else {
                    fromY = digThroughY(spaces, fromX, fromY, toY, digging);
                    fromX = digThroughX(spaces, fromX, fromY, toX, digging);
                }
            }
            prevX1 = x1;
            prevX2 = x2;
            prevY1 = y1;
            prevY2 = y2;
        }
        
    }

    private int digThroughY(Space[][] spaces, int fromX, int fromY, int toY, boolean digging) {
        while (fromY != toY) {
            if (fromY < toY){
                fromY++;
            } else if (fromY > toY){
                fromY--;
            }
            if (!digging && spaces[fromX][fromY].isOccupied()){
                if (randomNumber(0, 1) == 0){
                    spaces[fromX][fromY].setOccupant(new Door('-'));
                } else {
                    spaces[fromX][fromY].setOccupant(null);
                }
                digging = true;
            } else if (digging && spaces[fromX][fromY].isOccupied()){
                spaces[fromX][fromY].setOccupant(null);
            } else if (!digging && !spaces[fromX][fromY].isOccupied()){

            } else {
                break;
            }
        }
        return fromY;
    }

    private int digThroughX(Space[][] spaces, int fromX, int fromY, int toX, boolean digging) {
        while (fromX != toX) {
            if (fromX < toX){
                fromX++;
            } else if (fromX > toX){
                fromX--;
            }
            if (!digging && spaces[fromX][fromY].isOccupied()){
                if (randomNumber(0, 1) == 0){
                    spaces[fromX][fromY].setOccupant(new Door('|'));
                } else {
                    spaces[fromX][fromY].setOccupant(null);
                }
                digging = true;
            } else if (digging && spaces[fromX][fromY].isOccupied()){
                spaces[fromX][fromY].setOccupant(null);
            } else if (!digging && !spaces[fromX][fromY].isOccupied()){

            } else {
                break;
            }
        }
        return fromX;
    }

    protected void spawnEntites(Space[][] spaces, PlayerEntity playerEntity) {
        int points = 20 + depth * 10;
        Shopper<Entity> entityShopper = new Shopper<Entity>(points, Dungeon.getCurrentMonsterPool());
        while (entityShopper.hasPoints()) {
            getRandomUnoccupiedSpace(spaces).setOccupant(entityShopper.generate());
        }
        int chestPoints = (depth*10) + ((playerEntity.getMaxHP() - playerEntity.getHP()));
        Shopper<Chest> chestShopper = new Shopper<Chest>(chestPoints, Dungeon.getCurrentChestPool());
        while (chestShopper.hasPoints()) {
            getRandomUnoccupiedSpace(spaces).setOccupant(chestShopper.generate());
        }
        getRandomUnoccupiedSpace(spaces).addTerrain(new Staircase());
    }
    
    
    protected void spawnItems(Space[][] spaces) {

    }

    protected void spawnTerrains(Space[][] spaces) {

    }
    
    protected void spawnPlayer(Space[][] spaces, PlayerEntity playerEntity){
        Space rSpace = getRandomUnoccupiedSpace(spaces);
        while (rSpace == null){
            rSpace = getRandomUnoccupiedSpace(spaces);
        }
        rSpace.setOccupant(playerEntity);
    }

    @Override
    public void generateFloor(Space[][] spaces, PlayerEntity playerEntity) {
        generateSpaces(spaces);
        generateWalls(spaces);
        spawnEntites(spaces, playerEntity);
        // spawnItems(spaces);
        spawnTerrains(spaces);
        spawnPlayer(spaces, playerEntity);
    }

}
