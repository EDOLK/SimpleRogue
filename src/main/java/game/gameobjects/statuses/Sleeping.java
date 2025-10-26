package game.gameobjects.statuses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.display.Display;
import game.gamelogic.OverridesBehavable;
import game.gamelogic.Skill;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;
import game.gamelogic.combat.Attack;
import game.gamelogic.combat.AttackModifier;
import game.gamelogic.combat.PostAttackHook;
public class Sleeping extends Status implements OverridesBehavable, Behavable, AttackModifier {

    public Sleeping() {
        super();
        this.setCharacter(' ');
        this.setDescriptor("Sleeping");
        this.setFgColor(TileColor.create(66, 135, 245, 150));
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
                targetStealth += Skill.getSkill(Skill.STEALTH, target);
                animalPerception += Skill.getSkill(Skill.PERCEPTION, owner);
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
    public void modifyAttack(Attack attack) {
        attack.attachPostAttackHook(ar -> {
            Display.log("The " + owner.getName() + " wakes up.", owner.getSpace());
            owner.removeStatus(this);
        }, PostAttackHook.onHitted(owner));
    }
}
