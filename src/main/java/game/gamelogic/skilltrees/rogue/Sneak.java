package game.gamelogic.skilltrees.rogue;

import org.hexworks.zircon.api.color.TileColor;

import game.display.AbilitySelectMenu;
import game.display.Display;
import game.gamelogic.Levelable;
import game.gamelogic.ModifiesSkills;
import game.gamelogic.Skill;
import game.gamelogic.time.ModifiesMoveTime;
import game.gamelogic.abilities.Ability;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.statuses.Status;

public class Sneak implements Ability, Behavable, Levelable {

    private Entity owner;

    private int cooldown = 0;

    private int level = 1;

    public Sneak(Entity owner) {
        this.owner = owner;
    }

    @Override
    public String getName() {
        if (cooldown > 0) {
            return "Sneak (" + cooldown + ")";
        }
        return "Sneak";
    }

    @Override
    public int behave() {
        if (cooldown > 0) {
            cooldown--;
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void activate() {
        if (Display.getCurrentMenu() instanceof AbilitySelectMenu) {
            Display.revertMenu();
        }
        this.owner.addStatus(this.new Sneaking());
        this.cooldown = 50;
        Display.update();
    }

    @Override
    public boolean isEnabled() {
        return cooldown <= 0;
    }

    private class Sneaking extends Status implements Behavable, ModifiesSkills, ModifiesMoveTime{

        private int timer = 10 + (getLevel() * 5);

        private Sneaking() {
            super();
            setCharacter('░');
            setFgColor(TileColor.create(100, 100, 100, 255));
            setDescriptor("Sneaking");
        }

        @Override
        public int modifySkill(Skill skill, int value) {
            if (skill == Skill.STEALTH) {
                return (value + 1) * (getLevel()+1);
            }
            return value;
        }

        @Override
        public int behave() {
            timer--;
            if (timer <= 0) {
                if (this.owner instanceof PlayerEntity) {
                    Display.log("You stop sneaking.");
                }
                this.owner.removeStatus(this);
            }
            return 100;
        }

        @Override
        public boolean isActive() {
            return Status.isActiveHelper(this) && timer >= 0;
        }

        @Override
        public int modifyMoveTime(int time) {
            double p = 0.10 - (0.10*(getLevel()-1));
            return (int)(time + (time*p));
        }

        @Override
        public void onStatusAdd() {
            if (this.owner instanceof PlayerEntity) {
                Display.logHeader("You start sneaking.");
            }
        }

        
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean setLevel(int level) {
        this.level = level;
        return true;
    }

}
