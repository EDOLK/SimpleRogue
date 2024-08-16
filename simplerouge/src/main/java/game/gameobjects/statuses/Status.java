package game.gameobjects.statuses;

import game.gameobjects.DisplayableTile;
import game.gameobjects.entities.Entity;

public abstract class Status extends DisplayableTile{

    protected Entity owner;
    protected String descriptor;

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
    
}
