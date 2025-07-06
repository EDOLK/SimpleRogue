package game.gameobjects.statuses;

import game.gameobjects.DisplayableTile;
import game.gameobjects.entities.Entity;

public abstract class Status extends DisplayableTile{

    protected Entity owner;
    protected String descriptor;
    protected int xOffset;
    protected int yOffset;
    protected boolean fullBright;

    public int getxOffset() {
        return xOffset;
    }

    public void setxOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public boolean isFullBright() {
        return fullBright;
    }

    public void setFullBright(boolean fullBright) {
        this.fullBright = fullBright;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public Status(){
        super();
    }

    public static boolean isActiveHelper(Status status){
        return status.getOwner() != null && status.getOwner().isAlive();
    }
    
}
