package game.gameobjects.terrains;

import java.util.HashSet;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.SelfAware;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public class HiddenTrap extends Trap implements SelfAware{

    protected ExposedTrap exposedTrap;
    protected Space space;

    public HiddenTrap(ExposedTrap exposedTrap) {
        this.exposedTrap = exposedTrap;
        setCharacter(' ');
        setFgColor(TileColor.transparent());
        setBgColor(TileColor.transparent());
        setModifiers(new HashSet<Modifier>());
    }

    @Override
    public void triggerOnEntity(Entity entity) {
        exposedTrap.triggerOnEntity(entity);
        if (exposedTrap.isDestroyedOnTrigger()){
            getSpace().removeTerrain(this);
        }
    }

    @Override
    public boolean triggerableWhenAdding() {
        return exposedTrap.triggerableWhenAdding();
    }

    @Override
    public Space getSpace() {
        if (exposedTrap instanceof SelfAware selfAwareExposedTrap){
            return selfAwareExposedTrap.getSpace();
        } else {
            return space;
        }
    }

    @Override
    public void setSpace(Space space) {
        if (exposedTrap instanceof SelfAware selfAwareExposedTrap){
            selfAwareExposedTrap.setSpace(space);
        }
        this.space = space;
    }
}
