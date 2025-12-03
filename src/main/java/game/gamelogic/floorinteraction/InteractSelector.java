package game.gamelogic.floorinteraction;

import game.gamelogic.Interactable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.Terrain;

public class InteractSelector implements SimpleSelector{

    private Entity entity;

    public InteractSelector(Entity entity) {
        this.entity = entity;
    }

    @Override
    public SelectionResult simpleSelect(Space space) {

        if (space.isOccupied() && space.getOccupant() instanceof Interactable interactibleEntity){
            interactibleEntity.onInteract(entity);
            return new SelectionResult(true, 50);
        }

        for (Item item : space.getItems()) {
            if (item instanceof Interactable interactibleItem){
                interactibleItem.onInteract(entity);
                return new SelectionResult(true, 50);
            }
        }
        
        for (Terrain terrain : space.getTerrains()){
            if (terrain instanceof Interactable interactibleTerrain){
                interactibleTerrain.onInteract(entity);
                return new SelectionResult(true, 50);
            }
        }

        return new SelectionResult(true, 0);

    }

}
