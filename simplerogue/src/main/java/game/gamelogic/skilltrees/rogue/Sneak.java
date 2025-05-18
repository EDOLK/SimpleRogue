package game.gamelogic.skilltrees.rogue;
import game.gamelogic.abilities.Ability;
import game.gameobjects.entities.Entity;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.statuses.Status;
import game.gamelogic.ModifiesSkills;
import game.gamelogic.SkillMap.Skill;

public class Sneak implements Ability, Behavable{

    private int cooldown = 0;
    private Entity owner;

    public Sneak(Entity owner) {
        this.owner = owner;
    }

    @Override
    public String getName() {
        return "Sneak";
    }

    @Override
    public void activate() {
        owner.addStatus(new Sneaking());
    }

    @Override
    public boolean isEnabled() {
        return cooldown <= 0;
    }

    @Override
    public int behave() {
        cooldown--;
        return 100;
    }

    @Override
    public boolean isActive() {
        return cooldown > 0;
    }

    private class Sneaking extends Status implements Behavable, ModifiesSkills{

        private int timer = 10;

        @Override
        public int behave() {
            timer--;
            if (timer <= 0) {
                owner.removeStatus(this);
            }
            return 100;
        }

        @Override
        public boolean isActive() {
            return owner != null && owner.isAlive() && timer > 0;
        }

        @Override
        public int modifySkill(Skill skill, int value) {
            if (skill == Skill.STEALTH) {
                return value * 2;
            }
            return value;
        }

    }
}
