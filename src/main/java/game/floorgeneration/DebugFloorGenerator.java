package game.floorgeneration;

import java.util.function.Supplier;

import game.PathConditions;
import game.gamelogic.Examinable;
import game.gamelogic.Interactable;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.entities.Rat;
import game.gameobjects.entities.Wall;
import game.gameobjects.statuses.Flying;
import game.gameobjects.terrains.SpreadableTerrain;
import game.gameobjects.terrains.Terrain;

public class DebugFloorGenerator extends FloorGenerator {

    private Space[][] spaces;

    public DebugFloorGenerator(int depth) {
        super(depth);
    }

    @Override
    public void generateFloor(Space[][] spaces, PlayerEntity playerEntity) {
        this.spaces = spaces;
        generateSpaces();
        generateRectangle(1,1,48,48);
        spaces[20][20].setOccupant(playerEntity);
        Flying f = new Flying();
        f.setPerminent(true);
        playerEntity.addStatus(f);
        spaces[22][20].setOccupant(new Rat());
    }

    protected void generateSpaces(){
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                spaces[x][y] = new Space(x,y);
            }
        }
    }

    protected void generateRectangle(int x, int y, int height, int width) {

        for (int dx = x-1; dx <= x+width; dx++) {
            spaces[dx][y-1].setOccupant(new Wall());
            spaces[dx][y+height].setOccupant(new Wall());
        }

        for (int dy = y-1; dy <= y+height; dy++) {
            spaces[x-1][dy].setOccupant(new Wall());
            spaces[x+width][dy].setOccupant(new Wall());
        }

        for (int dx = x; dx < x+width; dx++) {
            for (int dy = y; dy < y+height; dy++) {
                spaces[dx][dy].setOccupant(null);
            }
        }

    }

    private class DebugSpreadable extends SpreadableTerrain implements Examinable {

        public DebugSpreadable(int amount) {
            super(amount);
        }

        @Override
        public char getCharacter() {
            return (char)(this.amount+48);
        }

        @Override
        protected SpreadableTerrain createSelf(int amount) {
            return new DebugSpreadable(amount);
        }

        @Override
        protected boolean isValidToSpread(Space potentialSpace) {
            return !potentialSpace.isOccupied() || !potentialSpace.getOccupant().isLiquidBlocker();
        }

        @Override
        public String getName() {
            return "Debug (" + this.amount + ")";
        }

        @Override
        public String getDescription() {
            return "Placeholder.";
        }

    }

    private class DebugSpace extends Space{

        private double d;
        private boolean forbidden;

        public DebugSpace(int x, int y, double d, boolean forbidden) {
            super("Question Mark", x, y);
            this.d = d;
            this.forbidden = forbidden;
        }

        public DebugSpace(int x, int y) {
            this(x,y,0,false);
        }

        public double getD() {
            return d;
        }

        public void setD(double d) {
            this.d = d;
        }

        public boolean isForbidden() {
            return forbidden;
        }

        public void setForbidden(boolean forbidden) {
            this.forbidden = forbidden;
        }

    }

    private class DebugRat extends Rat{

        public DebugRat(){
            super();
        }

        @Override
        public PathConditions getConditionsToEntity() {
            return new PathConditions().addDeterrentConditions(
                s -> {
                    return s instanceof DebugSpace ds ? ds.getD() : 0d;
                }
                ).addForbiddenConditions(
                s -> {
                    return s instanceof DebugSpace ds ? ds.isForbidden() : false;
                }
            );
        }

        @Override
        public PathConditions getConditionsToSpace() {
            return new PathConditions().addDeterrentConditions(
                s -> {
                    return s instanceof DebugSpace ds ? ds.getD() : 0d;
                }
                ).addForbiddenConditions(
                s -> {
                    return s instanceof DebugSpace ds ? ds.isForbidden() : false;
                }
            );
        }

    }

    private class DebugSpawner extends Terrain implements Examinable, Behavable, SelfAware, Interactable{

        private Supplier<Entity> generator;
        private int timer;
        private int rate;
        private Space space;
        private boolean toggled = true;

        public DebugSpawner(Supplier<Entity> generator, int rate) {
            this.generator = generator;
            this.rate = rate;
            this.timer = rate;
        }

        @Override
        public String getName() {
            return "Spawner";
        }

        @Override
        public int behave() {
            if (timer <= 0) {
                if (!getSpace().isOccupied()) {
                    getSpace().setOccupant(generator.get());
                }
                timer = rate;
            } else {
                timer --;
            }
            return 100;
        }

        @Override
        public boolean isActive() {
            return this.toggled;
        }

        @Override
        public char getCharacter() {
            String s = Integer.toString(timer);
            return s.charAt(s.length()-1);
        }

        @Override
        public String getDescription() {
            return "Debug Spawner: Spawning " + generator.get().getName() + ".";
        }

        @Override
        public Space getSpace() {
            return this.space;
        }

        @Override
        public void setSpace(Space space) {
            this.space = space;
        }

        @Override
        public void onInteract(Entity interactor) {
            this.toggled = !this.toggled;
        }

    }

}
