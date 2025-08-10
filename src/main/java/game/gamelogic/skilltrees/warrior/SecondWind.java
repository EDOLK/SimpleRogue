package game.gamelogic.skilltrees.warrior;

import game.display.Display;
import game.gamelogic.abilities.Ability;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHitted;
import game.gameobjects.entities.Entity;

public class SecondWind implements Ability, OnHitted, Behavable {

    private Entity owner;
    private int timer = 100;
    private int timeSinceHit;
    private int lastHitDamage;
    private boolean hit;

    public SecondWind(Entity owner) {
        this.owner = owner;
    }

    @Override
    public void activate() {
        owner.heal(Math.min(owner.getMaxHP()/2, lastHitDamage));
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
        return "Second Wind" + ( timer > 0 ? " (" + timer + ")" : "" );
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

}
