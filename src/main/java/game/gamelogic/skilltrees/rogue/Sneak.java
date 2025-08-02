package game.gamelogic.skilltrees.rogue;

import org.hexworks.zircon.api.color.TileColor;

import game.display.AbilitySelectMenu;
import game.display.Display;
import game.gamelogic.ModifiesSkills;
import game.gamelogic.Skill;
import game.gamelogic.time.ModifiesMoveTime;
import game.gamelogic.abilities.Ability;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.statuses.Status;

public class Sneak implements Ability, Behavable {

    private Entity owner;

    private int cooldown = 0;

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
        if (this.owner instanceof PlayerEntity) {
            Display.log("You start sneaking.");
        }
        this.owner.addStatus(new Sneaking());
        this.cooldown = 50;
        Display.update();
    }

    @Override
    public boolean isEnabled() {
        return cooldown <= 0;
    }

    private static class Sneaking extends Status implements Behavable, ModifiesSkills, ModifiesMoveTime{

        private int timer = 10;

        private Sneaking() {
            super();
            setCharacter('░');
            setfGColor(TileColor.create(100, 100, 100, 255));
            setDescriptor("Sneaking");
        }

        @Override
        public int modifySkill(Skill skill, int value) {
            if (skill == Skill.STEALTH) {
                return value*2;
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
            return timer >= 0;
        }

        @Override
        public int modifyMoveTime(int time) {
            return (int)(time + (time*0.10));
        }
        
    }

}
