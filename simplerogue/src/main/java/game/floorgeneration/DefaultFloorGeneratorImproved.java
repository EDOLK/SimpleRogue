package game.floorgeneration;

import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.Dungeon;
import game.Path;
import game.PathConditions;
import game.gameobjects.Space;
import game.gameobjects.entities.Chest;
import game.gameobjects.entities.Door;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.entities.Wall;
import game.gameobjects.terrains.Staircase;

public class DefaultFloorGeneratorImproved extends FloorGenerator {

    private Set<Space> madeSpaces = new HashSet<Space>();

    public DefaultFloorGeneratorImproved(int depth) {
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

        int roomNumber = 10 + (depth * 5);
        //int roomNumber = 10;
        int prevx1 = 0;
        int prevy1 = 0;
        int prevx2 = 0;
        int prevy2 = 0;
        int checks = 0;

        for (int i = 0; i < roomNumber; i ++){

            boolean valid;
            int roomWidth;
            int roomHeight;
            int x1;
            int y1;
            int x2;
            int y2;

            do {
                roomWidth = randomNumber(3, 10);
                roomHeight = randomNumber(3, 10);
                x1 = randomNumber(1, SIZE_X-1-roomWidth);
                y1 = randomNumber(1, SIZE_Y-1-roomHeight);
                x2 = x1+roomWidth;
                y2 = y1+roomHeight;
                valid = validateRoomability(spaces, x1, y1, x2, y2);
                checks += valid ? 1 : 0;
            } while (checks < 100 && !valid);

            if (checks >= 100) {
                checks = 0;
                continue;
            }


            for (int x = x1; x < x2; x++){
                for (int y = y1; y < y2; y++){
                    spaces[x][y].setOccupant(null);
                }
            }

            if (i != 0){

                //connect new room to previous room

                // x1, x2
                // y1, y2
                //
                // prevX1, prevX2
                // prevY1, prevY2

                List<Space> activeSpaces = getPerimeterSpacesWithCorners(spaces, x1, y1, x2, y2); 

                List<Space> currentActiveSpaces = getPerimeterSpaces(spaces, x1, y1, x2, y2); 

                List<Space> previousSpaces = getPerimeterSpacesWithCorners(spaces, prevx1, prevy1, prevx2, prevy2);

                List<Space> previousActiveSpaces = getPerimeterSpaces(spaces, prevx1, prevy1, prevx2, prevy2);

                Space[] path = null;

                while (path == null && !currentActiveSpaces.isEmpty() && !previousActiveSpaces.isEmpty()){
                    Space current = Pools.getRandom(currentActiveSpaces);
                    Space previous = Pools.getRandom(previousActiveSpaces);
                    try {
                        path = Path.getPathAsArray(
                            current,
                            previous,
                            spaces,
                            new PathConditions()
                                .removeForbiddenCondition(0)
                                .addForbiddenConditions(
                                    (space) -> {
                                        return activeSpaces.contains(space) || previousSpaces.contains(space) || space.getX() <= 0 || space.getY() <= 0 || space.getX() >= spaces.length-1 || space.getY() >= spaces[space.getX()].length-1;
                                    }
                                )
                                .setDiagonal(false)
                        );
                    } catch (Exception e) {
                        currentActiveSpaces.remove(current);
                        previousActiveSpaces.remove(previous);
                    }
                }

                if (path != null) {
                    dig(path);
                } else { 
                    System.out.println("FUCK");
                }

            }

            prevx1 = x1;
            prevx2 = x2;
            prevy1 = y1;
            prevy2 = y2;
        }
        
    }

    public void dig(Space[] path){
        for (Space space : path) {
            if (!space.isOccupied()) {
                return;
            }
            space.setOccupant(null);
        }
    }

    public boolean validateRoomability(Space[][] spaces, int x1, int y1, int x2, int y2){
        for (int x = x1-1; x < x2+1; x++){
            for (int y = y1-1; y < y2+1; y++){
                if (!spaces[x][y].isOccupied()){
                    return false;
                }
            }
        }
        return true;
    }

    public List<Space> getPerimeterSpaces(Space[][] spaces, int x1, int y1, int x2, int y2){
        List<Space> potentialSpaces = new ArrayList<>();

        for (int x = x1; x < x2; x++) {
            if (spaces[x][y1-1].isOccupied())
                potentialSpaces.add(spaces[x][y1-1]);
            if (spaces[x][y2].isOccupied())
                potentialSpaces.add(spaces[x][y2]);
        }
        for (int y = y1; y < y2; y++) {
            if (spaces[x1-1][y].isOccupied())
                potentialSpaces.add(spaces[x1-1][y]);
            if (spaces[x2][y].isOccupied())
                potentialSpaces.add(spaces[x2][y]);
        }

        return potentialSpaces;
    }

    public List<Space> getPerimeterSpacesWithCorners(Space[][] spaces, int x1, int y1, int x2, int y2){
        List<Space> potentialSpaces = new ArrayList<>();

        for (int x = x1-1; x <= x2; x++) {
            if (spaces[x][y1-1].isOccupied())
                potentialSpaces.add(spaces[x][y1-1]);
            if (spaces[x][y2].isOccupied())
                potentialSpaces.add(spaces[x][y2]);
        }
        for (int y = y1-1; y <= y2; y++) {
            if (spaces[x1-1][y].isOccupied())
                potentialSpaces.add(spaces[x1-1][y]);
            if (spaces[x2][y].isOccupied())
                potentialSpaces.add(spaces[x2][y]);
        }

        return potentialSpaces;
    }

    public Space getRandomPerimeterSpace(Space[][] spaces, int x1, int y1, int x2, int y2){

        return Pools.getRandom(getPerimeterSpaces(spaces, x1, y1, x2, y2));

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
        //spawnEntites(spaces, playerEntity);
        spawnPlayer(spaces, playerEntity);
    }

}
