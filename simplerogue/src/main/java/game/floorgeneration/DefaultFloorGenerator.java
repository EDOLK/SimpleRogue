package game.floorgeneration;

import static game.floorgeneration.Pools.getRandom;

import java.util.ArrayList;
import java.util.Collections;
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
import kotlin.Pair;

public class DefaultFloorGenerator extends FloorGenerator {

    protected Space[][] spaces;
    protected PlayerEntity player;
    protected int SIZE_X;
    protected int SIZE_Y;

    public DefaultFloorGenerator(int depth) {
        super(depth);
    }

    @Override
    public void generateFloor(Space[][] spaces, PlayerEntity playerEntity) {
        this.spaces = spaces;
        this.player = playerEntity;
        this.SIZE_X = spaces.length;
        this.SIZE_Y = spaces[0].length;
        generateSpaces();
        generateWalls();
        spawnEntities(generateRooms());
    }

    protected void generateSpaces(){
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                spaces[x][y] = new Space(x, y);
            }
        }
    }

    protected void generateWalls() {
        for (int x = 0; x < spaces.length; x++){
            for (int y = 0; y < spaces[x].length; y++){
                Wall wall = new Wall();
                spaces[x][y].setOccupant(wall);
                wall.setSpace(spaces[x][y]);
            }
        }
    }

    protected Pair<List<Room>,List<Space[]>> generateRooms() {
        int roomNumber = getRoomNumber(depth);
        Room previousRoom = null;
        int checks = 0;
        
        List<Room> rooms = new ArrayList<>();
        List<Space[]> paths = new ArrayList<>();

        for (int i = 0; i < roomNumber; i ++){

            boolean valid;
            Room currentRoom = null;

            do {
                currentRoom = generateRoom();
                valid = validateRoomability(currentRoom);
                checks += valid ? 1 : 0;
            } while (checks < 100 && !valid);

            if (checks >= 100 && !valid) {
                checks = 0;
                continue;
            }

            digOutRoom(currentRoom);

            if (i != 0){

                List<Space> currentSpaces = currentRoom.getRoomSpaces();

                List<Space> currentConnectorSpaces = new ArrayList<>(currentRoom.getConnectorSpaces());
                Collections.shuffle(currentConnectorSpaces);

                List<Space> previousSpaces = previousRoom.getRoomSpaces();

                List<Space> previousConnectorSpaces = new ArrayList<>(previousRoom.getConnectorSpaces());
                Collections.shuffle(previousConnectorSpaces);

                Space[] path = null;

                outer:
                for (Space cspace : currentConnectorSpaces) {
                    for (Space pspace : previousConnectorSpaces) {
                        try {
                            path = Path.getPathAsArray(
                                cspace,
                                pspace,
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
                            break outer;
                        } catch (Exception e) {

                        }
                    }
                }

                if (path != null) {
                    digOutPath(path);
                    paths.add(path);
                }
            }

            rooms.add(currentRoom);

            previousRoom = currentRoom;

        }
        return new Pair<List<Room>,List<Space[]>>(rooms,paths);
    }

    private void digOutRoom(Room room) {
        for (Space space : room.getInteriorSpaces()) {
            space.setOccupant(null);
        }
    }

    protected int getRoomNumber(int depth){
        return 10 + (depth * 5) <= 35 ? 10 + (depth * 5) : 35;
    }

    protected Room generateRoom() {
        return new SimpleRectRoom(spaces, SIZE_X, SIZE_Y);
    }

    protected void digOutPath(Space[] path){
        generateDoor(spaces, path[0]);
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
        generateDoor(spaces, path[path.length-1]);
    }

    private void generateDoor(Space[][] spaces, Space doorSpace) {
        if (spaces[doorSpace.getX()-1][doorSpace.getY()].isOccupied() && spaces[doorSpace.getX()+1][doorSpace.getY()].isOccupied()) {
            doorSpace.setOccupant(new Door('-'));
        } else {
            doorSpace.setOccupant(new Door('|'));
        }
    }

    protected boolean validateRoomability(Room room){
        for (Space space : room.getRoomSpaces()) {
            if (!space.isOccupied()) {
                return false;
            }
        }
        return true;
    }

    protected void spawnEntities(Pair<List<Room>,List<Space[]>> pair) {
        List<Room> rooms = new ArrayList<>(pair.getFirst());

        Room spawnRoom = rooms.get(0);

        getRandom(spawnRoom.getInteriorSpaces()).setOccupant(player);

        Shopper<Entity> entityShopper = new Shopper<Entity>(20 + depth * 10, Dungeon.getCurrentMonsterPool());

        rooms.remove(spawnRoom);

        while (entityShopper.hasPoints()) {
            Entity generated = entityShopper.generate();
            if (generated != null) {
                getRandom(getRandom(rooms).getInteriorSpaces()).setOccupant(generated);
            }
        }

        Shopper<Chest> chestShopper = new Shopper<Chest>((depth*10) + ((player.getMaxHP() - player.getHP())), Dungeon.getCurrentChestPool());

        while (chestShopper.hasPoints()) {
            Chest generated = chestShopper.generate();
            if (generated != null) {
                getRandom(getRandom(rooms).getInteriorSpaces()).setOccupant(generated);
            }
        }
      
        getRandom(pair.getFirst().get(pair.getFirst().size()-1).getInteriorSpaces()).addTerrain(new Staircase());

    }

}
