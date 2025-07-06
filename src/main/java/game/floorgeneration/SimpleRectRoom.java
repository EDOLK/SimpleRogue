package game.floorgeneration;

import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.List;

import game.gameobjects.Space;

class SimpleRectRoom implements Room{

    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private Space[][] spaces;

    public SimpleRectRoom(Space[][] spaces, int SIZE_X, int SIZE_Y) {
        int roomWidth = randomNumber(3,10);
        int roomHeight = randomNumber(3,10);
        this.x1 = randomNumber(1, SIZE_X-1-roomWidth);
        this.y1 = randomNumber(1, SIZE_Y-1-roomHeight);
        this.x2 = x1+roomWidth;
        this.y2 = y1+roomHeight;
        this.spaces = spaces;
    }

    @Override
    public List<Space> getRoomSpaces() {
        List<Space> rs = new ArrayList<Space>();
        for (int x = x1-1; x <= x2; x++) {
            for (int y = y1-1; y <= y2; y++) {
                rs.add(spaces[x][y]);
            }
        }
        return rs;
    }

    @Override
    public List<Space> getConnectorSpaces() {
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

    @Override
    public List<Space> getInteriorSpaces() {
        List<Space> potentialSpaces = new ArrayList<>();

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                potentialSpaces.add(spaces[x][y]);
            }
        }

        return potentialSpaces;
    }

}
