package game;

import java.util.Optional;

import game.gameobjects.Space;
import game.gameobjects.entities.Entity;

public class PathTracker {

    private PathFinder currentPathFinder;
    private PathConditions conditions;
    private Entity fromEntity;
    private Space toSpace;
    private Entity toEntity;
    private boolean trackingEntity = false;

    private PathTracker(Entity fromEntity, Entity toEntity, PathConditions conditions) throws Exception {
        this.toEntity = toEntity;
        this.fromEntity = fromEntity;
        this.trackingEntity = true;
        this.conditions = conditions;
        this.currentPathFinder = new PathFinder(fromEntity.getSpace(), toEntity.getSpace(), conditions);
    }

    private PathTracker(Entity fromEntity, Space toSpace, PathConditions conditions) throws Exception {
        this.fromEntity = fromEntity;
        this.toSpace = toSpace;
        this.conditions = conditions;
        this.currentPathFinder = new PathFinder(fromEntity.getSpace(), toSpace, conditions);
    }

    public static Optional<PathTracker> createPathTracker(Entity fromEntity, Space toSpace, PathConditions conditions){
        try {
            return Optional.of(new PathTracker(fromEntity, toSpace, conditions));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<PathTracker> createPathTracker(Entity fromEntity, Entity toEntity, PathConditions conditions){
        try {
            return Optional.of(new PathTracker(fromEntity, toEntity, conditions));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Space getNextSpace(){
        return currentPathFinder.getNext();
    }

    public boolean nextSpaceAvailable(){
        if (pathIsDone()) {
            return false;
        }
        if (pathIsClear() && entityIsPresent()) {
            return true;
        }
        return generatePathFinder();
    }

    private boolean entityIsPresent() {
        if (trackingEntity && entityIsMissing()){
            return false;
        }
        return true;
    }

    private boolean pathIsClear(){
        return currentPathFinder.pathIsClear();
    }

    private boolean pathIsDone(){
        return currentPathFinder.pathHasEnded();
    }

    private boolean entityIsMissing(){
        Space[] path = currentPathFinder.getPath();
        Space finalSpace = path[path.length-1];
        if (!finalSpace.isOccupied() || finalSpace.getOccupant() != toEntity) {
            return true;
        }
        return false;
    }

    private boolean generatePathFinder() {
        if (trackingEntity) {
            try {
                this.currentPathFinder = new PathFinder(fromEntity.getSpace(), toEntity.getSpace(), conditions);
            } catch (Exception e) {
                return false;
            }
        } else {
            try {
                this.currentPathFinder = new PathFinder(fromEntity.getSpace(), toSpace, conditions);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

}
