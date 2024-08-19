package game.gameobjects.terrains.liquids;

import static game.App.lerp;

import java.util.ArrayList;
import java.util.Collections;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.gamelogic.Examinable;
import game.gamelogic.SelfAware;
import game.gamelogic.Triggerable;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Wet;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.gasses.Gas;

public abstract class Liquid extends Terrain implements Examinable, SelfAware, Behavable, Triggerable{
    
    public static final int MAX_DEPTH = 20;
    protected int evaporationRate = 50;
    protected int turns = 0;
    protected int depth;
    protected double viscosity;
    protected Space space;
    protected String name = "Placeholder Name";
    protected String description = "Placeholder description.";
    protected int minOpacity = 0;
    protected int maxOpacity = 255;

    public Liquid(int depth){
        super();
        setDepth(depth);
    }

    public double getViscosity() {
        return viscosity;
    }
    
    public void setViscosity(double viscosity) {
        if (viscosity >= 0 || viscosity <= 1){
            this.viscosity = viscosity;
        }
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getEvaporationRate() {
        return evaporationRate;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        if (depth < 0){
            this.depth = 0;
        } else if (depth > MAX_DEPTH){
            this.depth = MAX_DEPTH;
        } else {
            this.depth = depth;
        }
    }

    @Override
    public TileColor getbGColor() {
        return super.getbGColor().withAlpha((int)lerp(0, minOpacity, MAX_DEPTH, maxOpacity, depth));
    }

    public void addDepth(int depth){
        this.depth += depth;
        if (this.depth >= MAX_DEPTH){
            this.depth = MAX_DEPTH;
        }
    }

    public void subtractDepth(int depth){
        this.depth -= depth;
        if (this.depth <= 0){
            this.depth = 0;
        }
    }
    
    @Override
    public void behave() {
        
        if (depth <= 0){
            getSpace().getTerrains().remove(this);
            setSpace(null);
            return;
        }

        ArrayList<Space> viableSpaces = getViableSpaces();

        while (true) {

            Collections.shuffle(viableSpaces);

            boolean spread = false;

            for (Space space : viableSpaces) {
                if (getLiquid(space) == null || (getLiquid(space).getDepth() < depth)){
                    spread = true;
                    Liquid l = createSelf(1);
                    l.turns = this.turns;
                    space.addLiquid(l);
                    depth--;
                    if (depth <= 0){
                        getSpace().getTerrains().remove(this);
                        setSpace(null);
                        return;
                    }
                }
            }
            
            if (!spread){
                break;
            }

        }

        if (turns >= evaporationRate){
            depth--;
            turns = 0;
        } else {
            turns ++;
        }

    }

    @Override
    public boolean isActive() {
        return getSpace() != null;
    }

    @Override
    public Space getSpace() {
        return space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public String getName() {
        return name + " (" + depth + ")";
    }
    
    public String getTrueName(){
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public final Liquid getLiquid(Space space){
        Class<? extends Liquid> thisClass = this.getClass();
        for (Terrain terrain : space.getTerrains()) {
            if (terrain instanceof Liquid liquid && thisClass.isInstance(liquid)){
                return liquid;
            }
        }
        return null;
    }

    public abstract Liquid createSelf(int depth);

    public abstract Gas getEvaporationGas(int amount);
    
    public abstract Terrain getFreezingTerrain(int amount);
    
    public abstract boolean evaporates();

    public abstract boolean freezes();

    @Override
    public void triggerOnEntity(Entity entity) {
        entity.addStatus(new Wet(this));
    }

    @Override
    public boolean triggerableWhenAdding() {
        return true;
    }

    protected ArrayList<Space> getViableSpaces(){
        ArrayList<Space> spaces = new ArrayList<Space>();
        outer:
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++){
                if (depth <= 1){
                    break outer;
                }
                if (i == 0 && j == 0){
                    continue;
                }
                int x = getSpace().getX() + i;
                int y = getSpace().getY() + j;
                try {
                    Space possibleSpace = Dungeon.getCurrentFloor().getSpace(x, y);
                    if (possibleSpace.isOccupied() && possibleSpace.getOccupant().isLiquidBlocker()){
                        continue;
                    }
                    if (this.getLiquid(possibleSpace) == null && Math.random() <= viscosity){
                        continue;
                    }
                    spaces.add(possibleSpace);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return spaces;
    }
    
    
}
