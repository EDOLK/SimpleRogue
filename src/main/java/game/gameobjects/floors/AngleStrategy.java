package game.gameobjects.floors;

import java.util.Optional;

import game.gamelogic.Angle;
import game.gameobjects.Space;

public class AngleStrategy implements SpaceGetterStrategy {

    private int x;
    private int y;
    private Angle angle;
    private int offset;

    public AngleStrategy(int x, int y, Angle angle, int offset) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.offset = offset;
    }

    public AngleStrategy(Space space, Angle angle, int offset) {
        this(space.getX(), space.getY(), angle, offset);
    }

    @Override
    public Optional<Space> getSpace(Floor floor) {
        int degree = angle.getDegree();
        double radians = Math.toRadians(degree);
        int xOffset = (int)(Math.cos(radians) * offset);
        int yOffset = (int)(Math.sin(radians) * offset)*-1;
        try {
            return Optional.of(floor.getSpace(x + xOffset, y + yOffset));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
