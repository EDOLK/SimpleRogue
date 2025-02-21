package game.gamelogic.abilities;

import game.gamelogic.OverridesAttack;
import game.gamelogic.OverridesMovement;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHitted;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Status;

import static game.App.randomNumber;

import game.display.Display;

public class Meditate implements Ability, Behavable{

    private Entity owner;
    private boolean attached = false;
    private int timer = 100;

    public Meditate(Entity owner) {
        super();
        this.owner = owner;
    }

    @Override
    public String getName() {
        return "Meditate" + (timer > 0 ? "(" + timer + "/" + "100" + ")" : "");
    }

    @Override
    public void activate(){
        Meditating m = this.new Meditating();
        this.owner.addStatus(m);
        this.timer = 100;
        this.attached = true;
        Display.log("You begin meditating.");
        Display.revertMenu();
    }

    @Override
    public boolean isEnabled() {
        return timer <= 0;
    }

    @Override
    public void behave() {
        timer--;
        if (timer <= 0) {
            Display.log("Meditate is ready.");
        }
    }

    @Override
    public boolean isActive() {
        return !attached && timer > 0;
    }

    private class Meditating extends Status implements Behavable, OverridesMovement, OverridesAttack, OnHitted {

        private int timer = 15;

        @Override
        public void overrideAttack(Entity attacker, Entity attackee) {
            detach();
            Floor.doAttack(attacker,attackee);
        }


        @Override
        public boolean overrideMovement(Entity entity, Space toSpace) {
            detach();
            return Space.moveEntity(entity, toSpace);
        }

        @Override
        public void behave() {
            // getOwner().heal(randomNumber(0,3));
            timer--;
            if (getOwner().getHP() >= getOwner().getMaxHP() || timer <= 0) {
                detach();
            }
        }

        private void detach() {
            getOwner().removeStatus(this);
            Meditate.this.attached = false;
        }

        @Override
        public boolean isActive() {
            return true;
        }


        @Override
        public void doOnHitted(Entity self, Entity other, AttackInfo attackInfo) {
            Display.log("The pain disrupts your meditation.");
            detach();
        }
        
    }
    
}
