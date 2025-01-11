package game.gameobjects;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.OverridesMovement;
import game.gamelogic.SelfAware;
import game.gamelogic.Triggerable;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.Fire;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.gasses.Gas;
import game.gameobjects.terrains.liquids.Liquid;

public class Space extends DisplayableTile{

    public static class SpaceOccupiedException extends Exception {

        private final Entity occupant;
        private final Space occupiedSpace;

        public SpaceOccupiedException(Entity occupant){
            this.occupant = occupant;
            this.occupiedSpace = occupant.getSpace();
        }

        public SpaceOccupiedException(Space space){
            this.occupiedSpace = space;
            this.occupant = space.getOccupant();
        }

        public Entity getOccupant() {
            return occupant;
        }

        public Space getOccupiedSpace(){
            return occupiedSpace;
        }

    }

    public static boolean moveEntity(Entity entity, Space toSpace){
        OverridesMovement overridesMovement = (OverridesMovement)entity.getStatusByClass(OverridesMovement.class);
        if (overridesMovement != null && overridesMovement.isEnabled()){
            return overridesMovement.overrideMovement(entity, toSpace);
        }

        if (toSpace.isOccupied()){
            return false;
        }

        Space prevSpace = entity.getSpace();
        toSpace.setOccupant(entity);
        entity.setSpace(toSpace);

        if (prevSpace != null){
            prevSpace.setOccupant(null);
        }

        doTriggerables(entity, toSpace);
        
        return true;

    }

    // public static void swapEntity(Entity entity, Entity otherEntity){
    //     Space space = entity.getSpace();
    //     Space otherSpace = otherEntity.getSpace();
    //     space.setOccupant(otherEntity);
    //     otherSpace.setOccupant(entity);
    //     doTriggerables(entity, otherSpace);
    //     doTriggerables(otherEntity, space);
    // }

    private static void doTriggerables(Entity entity, Space toSpace) {
        List<Triggerable> triggerables = new ArrayList<Triggerable>();
        for (Item item : toSpace.getItems()) {
            if (item instanceof Triggerable triggerableItem){
                triggerables.add(triggerableItem);
            }
        }
        for (Terrain terrain : toSpace.getTerrains()) {
            if (terrain instanceof Triggerable triggerableTerrain){
                triggerables.add(triggerableTerrain);
            }
        }
        for (Triggerable triggerable : triggerables) {
            triggerable.triggerOnEntity(entity);
        }
    }

    private Entity occupant;
    private ArrayList<Item> items;
    private ArrayList<Terrain> terrains;
    private float light = 0.0f;

    private final int X;
    private final int Y;

    public Space(int x, int y){
        // this(TileColor.create(255,255,255,255), TileColor.create(0, 0, 0, 255), '█', x, y);
        this(TileColor.create(0, 0, 0, 0), TileColor.create(60, 60, 60, 255), '.', x, y);
    }

    public Space(TileColor bGColor, TileColor fGColor, char character, String tileName, int x, int y) {
        super(bGColor, fGColor, character);
        this.X = x;
        this.Y = y;
        setOccupant(null);
        setTileName(tileName);
        items = new ArrayList<Item>();
        terrains = new ArrayList<Terrain>();
    }

    public Space(TileColor bGColor, TileColor fGColor, char character, int x, int y) {
        this(bGColor, fGColor, character, "Space", x, y);
    }

    public Space(String tileName, int x, int y) {
        this(TileColor.create(0, 0, 0, 0), TileColor.create(60, 60, 60, 255), '.', tileName, x, y);
    }
    
    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }
    
    public List<Terrain> getTerrains(){
        return List.copyOf(terrains);
    }
    
    public boolean removeTerrain(Terrain terrain){
        return terrains.remove(terrain);
    }

    public boolean addGas(Gas gas){
        Gas otherGas = gas.getGas(this);
        if (otherGas != null){
            otherGas.addDensity(gas.getDensity());
            return true;
        } else {
            return addTerrain(gas);
        }
    }

    public boolean addLiquid(Liquid liquid){
        Liquid otherLiquid = liquid.getLiquid(this);
        if (otherLiquid != null){
            otherLiquid.addDepth(liquid.getDepth());
            return true;
        } else {
            return addTerrain(liquid);
        }
    }

    public boolean addFire(Fire fire){
        for (Terrain terrain : terrains) {
            if (terrain instanceof Fire){
                return false;
            }
        }
        return addTerrain(fire);
    }
    
    public List<Item> getItems() {
        return List.copyOf(items);
    }

    public boolean addItem(Item item){
        if (item != null){
            if (item instanceof SelfAware selfAware){
                selfAware.setSpace(this);
            }
            if (item instanceof Triggerable triggerableItem && triggerableItem.triggerableWhenAdding() && this.isOccupied()){
                triggerableItem.triggerOnEntity(occupant);
            }
            return items.add(item);
        }
        return false;
    }
    
    public boolean removeItem(Item item){
        return items.remove(item);
    }

    public int getX(){
        return X;
    }

    public int getY(){
        return Y;
    }

    public boolean isOccupied() {
        return occupant != null;
    }

    public Entity getOccupant() {
        return occupant;
    }

    public void setOccupant(Entity occupant) {
        this.occupant = occupant;
        if (occupant instanceof SelfAware selfAware){
            selfAware.setSpace(this);
        }
    }

    public boolean addTerrain(Terrain terrain){
        if (terrain != null){
            if (terrain instanceof SelfAware selfAware){
                selfAware.setSpace(this);
            }
            if (terrain instanceof Triggerable triggerableTerrain && triggerableTerrain.triggerableWhenAdding() && this.isOccupied()){
                triggerableTerrain.triggerOnEntity(occupant);
            }
            return terrains.add(terrain);
        }
        return false;
    }
    
    public boolean hasTerrainType(Class<? extends Terrain> clazz){
        for (Terrain terrain : terrains) {
            if (clazz.isInstance(terrain))
                return true;
        }
        return false;
    }
    
    public boolean remove(Object object){
        boolean removed = removeHelper(object);
        if (object instanceof SelfAware selfAware && removed){
            selfAware.setSpace(null);
        }
        return removed;
    }

    private boolean removeHelper(Object object){
        switch (object) {
            case Terrain terrain -> {
                return removeTerrain(terrain);
            }
            case Item item -> {
                return removeItem(item);
            }
            case Entity entity ->{
                this.setOccupant(null);
                return true;
            }
            default ->{
                return false;                
            }
        }
    }
    
}
    
