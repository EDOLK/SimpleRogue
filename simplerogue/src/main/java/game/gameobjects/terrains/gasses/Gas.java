package game.gameobjects.terrains.gasses;

import static game.App.*;

import java.util.ArrayList;
import java.util.Collections;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.gamelogic.Examinable;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.liquids.Liquid;

public abstract class Gas extends Terrain implements Examinable, SelfAware, Behavable{

    public static final int MAX_DENSITY = 10;
    private int density;
    protected double spreadFactor = 0.1;
    protected int disapparationRate = 50;
    protected int turns;
    protected String name = "Placeholder Name";
    protected String description = "Placeholder description.";
    protected int minOpacity;
    protected int maxOpacity;

    public Gas(int density){
        super();
        setDensity(density);
        this.minOpacity = 0;
        this.maxOpacity = 255;
    }

    @Override
    public TileColor getbGColor() {
        return super.getbGColor().withAlpha((int)lerp(0, minOpacity, MAX_DENSITY, maxOpacity, density));
    }

    public double getSpreadFactor() {
        return spreadFactor;
    }

    public void setSpreadFactor(double spreadFactor) {
        this.spreadFactor = spreadFactor;
    }

    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        if (density < 0){
            this.density = 0;
        } else if (density > MAX_DENSITY){
            this.density = MAX_DENSITY;
        } else {
            this.density = density;
        }
    }
    
    public void addDensity(int density){
        this.density += density;
        if (this.density >= MAX_DENSITY){
            this.density = MAX_DENSITY;
        }
    }
    
    public void removeDensity(int density){
        this.density -= density;
        if (this.density <= 0){
            this.density = 0;
        }
    }

    public String getName() {
        return name + " (" + density + ")";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMAX_DENSITY() {
        return MAX_DENSITY;
    }
    
    public final Gas getGas(Space space){
        Class<? extends Gas> thisClass = this.getClass();
        for (Terrain terrain : space.getTerrains()) {
            if (terrain instanceof Gas gas && thisClass.isInstance(gas)){
                return gas;
            }
        }
        return null;
    }
    
    protected ArrayList<Space> getViableSpaces(){
        ArrayList<Space> spaces = new ArrayList<Space>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0){
                    continue;
                }
                int x = getSpace().getX() + i;
                int y = getSpace().getY() + j;
                try {
                    Space possibleSpace = Dungeon.getCurrentFloor().getSpace(x, y);
                    if (possibleSpace.isOccupied() && possibleSpace.getOccupant().isGasBlocker()){
                        continue;
                    }
                    if (this.getGas(possibleSpace) == null && Math.random() > spreadFactor){
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

    @Override
    public void behave() {

        if (turns >= disapparationRate){
            density--;
            turns = 0;
        } else {
            turns++;
        }

        if (density <= 0){
            getSpace().getTerrains().remove(this);
            setSpace(null);
            return;
        }

        ArrayList<Space> viableSpaces = getViableSpaces();

        while (true) {

            Collections.shuffle(viableSpaces);

            boolean spread = false;

            for (Space space : viableSpaces) {
                if (getGas(space) == null || (getGas(space).getDensity() < density && getGas(space).getDensity() + 1 <= density-1)){
                    spread = true;
                    Gas gas = createSelf(1);
                    gas.turns = this.turns;
                    space.addGas(gas);
                    density--;
                    if (density <= 0){
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
        
    }

    @Override
    public boolean isActive() {
        return getSpace() != null;
    }
    
    protected abstract Gas createSelf(int density);
    
    public abstract Liquid getCondensationLiquid(int depth);
    
    public abstract boolean condenses();

}
