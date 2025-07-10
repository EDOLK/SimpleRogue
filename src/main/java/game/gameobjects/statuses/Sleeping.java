package game.gameobjects.statuses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.display.Display;
import game.gamelogic.HasSkills;
import game.gamelogic.OverridesBehavable;
import game.gamelogic.Skill;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHitted;
public class Sleeping extends Status implements OverridesBehavable, Behavable, OnHitted {

    public Sleeping() {
        super();
        this.setCharacter(' ');
        this.setDescriptor("Sleeping");
        this.setfGColor(TileColor.create(66, 135, 245, 150));
        this.setModifiers(new HashSet<>(List.of(Modifiers.blink())));
    }

    @Override
    public int overrideBehave(Behavable behavable) {
        return owner.getTimeToWait();
    }

    @Override
    public boolean overrideIsActive(Behavable behavable) {
        return owner != null && owner.isAlive();
    }

    @Override
    public int behave() {
        for (Entity target : checkForTargets()) {
            if (target != null) {
                int targetStealth = Math.max(App.randomNumber(1,20), App.randomNumber(1,20));
                int animalPerception = 10;
                if (target instanceof HasSkills hasSkills) {
                    targetStealth += hasSkills.getSkill(Skill.STEALTH);
                }
                if (owner instanceof HasSkills hasSkills) {
                    animalPerception += hasSkills.getSkill(Skill.PERCEPTION);
                }
                targetStealth += (int)((target.getSpace().getLight()-0.50f)*-15);
                int distance = Space.getDistance(owner.getSpace(), target.getSpace());
                if (distance <= 5) {
                    animalPerception += Math.abs(distance-6);
                }
                if (animalPerception >= targetStealth) {
                    Display.log("The " + owner.getName() + " wakes up.", owner.getSpace());
                    owner.removeStatus(this);
                    return 100;
                }
            }
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }

    protected List<Entity> checkForTargets(){
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : owner.getEntitiesInVision()) {
            if (owner instanceof Animal animal && animal.isEnemy(entity)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public void doOnHitted(Entity self, Entity other, AttackInfo attackInfo) {
        Display.log("The " + owner.getName() + " wakes up.", owner.getSpace());
        owner.removeStatus(this);
    }
}
