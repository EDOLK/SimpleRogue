package game.gamelogic.passives;

import game.gamelogic.HasMemory;
import game.gamelogic.Skill;
import game.gamelogic.abilities.Passive;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.behavior.HasEnemies;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Sleeping;

public class MemoryDetector implements Passive, Behavable {

    private HasMemory owner;

    public MemoryDetector(HasMemory owner) {
        this.owner = owner;
    }

    @Override
    public int behave() {
        if (owner instanceof Entity entity) {
            for (Entity e : entity.getEntitiesInVision()) {
                if (!(owner instanceof HasEnemies he) || he.isEnemy(e)) {
                    if (owner.getFromMemory(e).isPresent() || Skill.stealthCheck(entity, e) == entity) {
                        owner.addToMemory(e);
                    }
                }
            }
        }
        return owner.getRememberTime();
    }

    @Override
    public boolean isActive() {
        return owner != null && (owner instanceof Entity entity ? entity.isAlive() && entity.getStatusByClass(Sleeping.class) == null : true);
    }

    
}
