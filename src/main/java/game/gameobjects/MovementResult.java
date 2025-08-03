package game.gameobjects;

import java.util.List;

import game.gamelogic.OverridesMovement;
import game.gamelogic.Triggerable;
import game.gameobjects.entities.Entity;

public class MovementResult {

    private boolean successful;
    private Space fromSpace;
    private Space toSpace;
    private Entity mover;
    private Entity blocker;
    private OverridesMovement override;
    private List<Triggerable> triggerables;

    public MovementResult withTriggerables(List<Triggerable> triggerables) {
        this.triggerables = triggerables;
        return this;
    }
    public MovementResult withSuccessful(boolean successful) {
        this.successful = successful;
        return this;
    }
    public MovementResult withFromSpace(Space fromSpace) {
        this.fromSpace = fromSpace;
        return this;
    }
    public MovementResult withToSpace(Space toSpace) {
        this.toSpace = toSpace;
        return this;
    }
    public MovementResult withMover(Entity mover) {
        this.mover = mover;
        return this;
    }
    public MovementResult withBlocker(Entity blocker) {
        this.blocker = blocker;
        return this;
    }
    public MovementResult withOverride(OverridesMovement override) {
        this.override = override;
        return this;
    }

    public boolean isSuccessful() {
        return successful;
    }
    public Space getFromSpace() {
        return fromSpace;
    }
    public Space getToSpace() {
        return toSpace;
    }
    public Entity getMover() {
        return mover;
    }
    public Entity getBlocker() {
        return blocker;
    }
    public OverridesMovement getOverride() {
        return override;
    }
    public List<Triggerable> getTriggerables() {
        return triggerables;
    }
    public boolean hasBlocker(){
        return getBlocker() != null;
    }
    public boolean hasOverride(){
        return getOverride() != null;
    }

}
