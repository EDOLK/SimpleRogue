package game.gamelogic.skilltrees.warrior;

import java.util.HashSet;
import java.util.List;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.display.Display;
import game.gamelogic.abilities.Ability;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHitted;
import game.gamelogic.time.ModifiesAttackTime;
import game.gamelogic.time.ModifiesMoveTime;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Status;

public class SecondWind implements Ability, OnHitted, Behavable {

    private Entity owner;
    private int timer;
    private int timeSinceHit;
    private int lastHitDamage;
    private boolean hit;

    public SecondWind(Entity owner) {
        this.owner = owner;
    }

    @Override
    public void activate() {
        owner.heal(Math.min(owner.getMaxHP()/2, lastHitDamage));
        owner.addStatus(new SecondWindStatus());
        hit = false;
        timeSinceHit = 0;
        lastHitDamage = 0;
        timer = 100;
        Display.revertMenu();
    }

    @Override
    public boolean isEnabled() {
        return timer <= 0 && hit && timeSinceHit <= 5;
    }

    @Override
    public String getName() {
        return String.join(
            "",
            "Second Wind",
            isEnabled() ? (lastHitDamage > 0 ? " x" + lastHitDamage : "") : "",
            isEnabled() ? (" -" + (5 - timeSinceHit)) : "",
            (timer > 0 ? " (" + timer + ")" : "")
        );
    }

    @Override
    public void doOnHitted(Entity self, Entity other, AttackInfo attackInfo) {
        if (attackInfo.getDamageDelt() > 0) {
            hit = true;
            lastHitDamage = attackInfo.getDamageDelt();
            timeSinceHit = 0;
        }
    }

    @Override
    public int behave() {
        if (timer > 0) {
            timer--;
        }
        if (hit) {
            timeSinceHit++;
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return owner != null;
    }

    private static class SecondWindStatus extends Status implements ModifiesMoveTime, ModifiesAttackTime, Behavable {
        private int turns = 1;

        public SecondWindStatus() {
            super();
            setCharacter('>');
            setbGColor(TileColor.transparent());
            setfGColor(TileColor.create(255, 255, 255, 255));
            setModifiers(new HashSet<Modifier>(List.of(Modifiers.blink())));
            setDescriptor("");
        }

        @Override
        public int modifyAttackTime(int time) {
            return time - (int)(time*.50);
        }

        @Override
        public int modifyMoveTime(int time) {
            return time - (int)(time*.50);
        }

        @Override
        public int behave() {
            if (turns > 0) {
                turns--;
            } else {
                this.owner.removeStatus(this);
            }
            return 100;
        }

        @Override
        public boolean isActive() {
            return Status.isActiveHelper(this);
        }

        
    }

}
