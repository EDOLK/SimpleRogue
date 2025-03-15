package game.floorgeneration;

import java.util.ArrayList;
import java.util.List;

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

public class DefaultFloorGenerator extends FloorGenerator {

    private Space[][] spaces;
    private PlayerEntity player;
    private int SIZE_X;
    private int SIZE_Y;

    public DefaultFloorGenerator(int depth) {
        super(depth);
    }

    public void generateSpaces(){
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                spaces[x][y] = new Space(x, y);
            }
        }
    }

    public void generateWalls() {
        for (int x = 0; x < spaces.length; x++){
            for (int y = 0; y < spaces[x].length; y++){
                Wall wall = new Wall();
                spaces[x][y].setOccupant(wall);
                wall.setSpace(spaces[x][y]);
            }
        }

        int roomNumber = getRoomNumber(depth);
        RoomBlueprint previousRoom = null;
        int checks = 0;

        for (int i = 0; i < roomNumber; i ++){

            boolean valid;
            RoomBlueprint currentRoom = null;

            do {
                currentRoom = generateRoom();
                valid = validateRoomability(currentRoom);
                checks += valid ? 1 : 0;
            } while (checks < 100 && !valid);

            if (checks >= 100) {
                checks = 0;
                continue;
            }


            for (Space space : currentRoom.getInteriorSpaces()) {
                space.setOccupant(null);
            }

            if (i != 0){

                List<Space> currentSpaces = currentRoom.getRoomSpaces();

                List<Space> currentConnectorSpaces = currentRoom.getConnectorSpaces();

                List<Space> previousSpaces = previousRoom.getRoomSpaces();

                List<Space> previousConnectorSpaces = previousRoom.getConnectorSpaces();

                Space[] path = null;

                while (path == null && !currentConnectorSpaces.isEmpty() && !previousConnectorSpaces.isEmpty()){
                    Space current = Pools.getRandom(currentConnectorSpaces);
                    Space previous = Pools.getRandom(previousConnectorSpaces);
                    try {
                        path = Path.getPathAsArray(
                            current,
                            previous,
                            spaces,
                            new PathConditions()
                                .removeForbiddenCondition(0)
                                .addForbiddenConditions(
                                    (space) -> {
                                        return
                                            currentSpaces.contains(space) ||
                                            previousSpaces.contains(space) ||
                                            space.getX() <= 0 ||
                                            space.getY() <= 0 ||
                                            space.getX() >= spaces.length-1 ||
                                            space.getY() >= spaces[space.getX()].length-1;
                                    }
                                )
                                .setDiagonal(false)
                                .setHFunction((from, to) -> {
                                    return (double) Math.min(Math.abs(from.getX() - to.getX()), Math.abs(from.getY() - to.getY()));
                                })
                        );
                    } catch (Exception e) {

                    }
                }

                if (path != null) {
                    dig(path);
                }

                if (i == roomNumber-1){
                    Pools.getRandom(currentRoom.getInteriorSpaces()).addTerrain(new Staircase());
                }

            }

            previousRoom = currentRoom;

        }
        
    }

    @Override
    protected int getRoomNumber(int depth){
        return 10 + (depth * 5) <= 35 ? 10 + (depth * 5) : 35;
    }

    @Override
    protected RoomBlueprint generateRoom() {
        return new SimpleRectRoom(spaces, SIZE_X, SIZE_Y);
    }

    public void dig(Space[] path){
        Space doorSpace = path[0];
        generateDoor(spaces, doorSpace);
        for (int i = 1; i < path.length; i++) {
            Space prevSpace = path[i-1];
            Space space = path[i];
            List<Space> adjSpaces = new ArrayList<>();
            adjSpaces.addAll(List.of(spaces[space.getX()-1][space.getY()],spaces[space.getX()+1][space.getY()],spaces[space.getX()][space.getY()+1],spaces[space.getX()][space.getY()-1]));
            adjSpaces.remove(prevSpace);
            if (!space.isOccupied()) {
                return;
            }
            for (Space s : adjSpaces) {
                if (!s.isOccupied()) {
                    space.setOccupant(null);
                    return;
                }
            }
            space.setOccupant(null);
        }
    }

    private void generateDoor(Space[][] spaces, Space doorSpace) {
        if (spaces[doorSpace.getX()-1][doorSpace.getY()].isOccupied() && spaces[doorSpace.getX()+1][doorSpace.getY()].isOccupied()) {
            doorSpace.setOccupant(new Door('-'));
        } else {
            doorSpace.setOccupant(new Door('|'));
        }
    }

    public boolean validateRoomability(RoomBlueprint room){
        for (Space space : room.getRoomSpaces()) {
            if (!space.isOccupied()) {
                return false;
            }
        }
        return true;
    }

    protected void spawnEntites() {
        int points = 20 + depth * 10;
        Shopper<Entity> entityShopper = new Shopper<Entity>(points, Dungeon.getCurrentMonsterPool());
        while (entityShopper.hasPoints()) {
            getRandomUnoccupiedSpace(spaces).setOccupant(entityShopper.generate());
        }
        int chestPoints = (depth*10) + ((player.getMaxHP() - player.getHP()));
        Shopper<Chest> chestShopper = new Shopper<Chest>(chestPoints, Dungeon.getCurrentChestPool());
        while (chestShopper.hasPoints()) {
            getRandomUnoccupiedSpace(spaces).setOccupant(chestShopper.generate());
        }
    }
    
    protected void spawnPlayer(){
        Space rSpace = getRandomUnoccupiedSpace(spaces);
        while (rSpace == null){
            rSpace = getRandomUnoccupiedSpace(spaces);
        }
        rSpace.setOccupant(this.player);
    }

    @Override
    public void generateFloor(Space[][] spaces, PlayerEntity playerEntity) {
        this.spaces = spaces;
        this.player = playerEntity;
        this.SIZE_X = spaces.length;
        this.SIZE_Y = spaces[0].length;
        generateSpaces();
        generateWalls();
        spawnEntites();
        spawnPlayer();
    }

}
