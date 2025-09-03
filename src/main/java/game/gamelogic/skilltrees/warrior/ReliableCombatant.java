package game.gamelogic.skilltrees.warrior;

import game.Dungeon;
import game.display.Display;
import game.gamelogic.Levelable;
import game.gamelogic.ModifiesAccuracy;
import game.gamelogic.abilities.Ability;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHit;
import game.gameobjects.Floor;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;

public class ReliableCombatant implements Ability, Behavable, ModifiesAccuracy, Levelable, OnHit {

    private int combo = 0;
    private int level = 1;
    private boolean hit;
    private Entity owner;
    private int turns = 0;
    private int decay = getDecayRate();

    public ReliableCombatant(Entity owner) {
        this.owner = owner;
    }

    @Override
    public int behave() {
        if (!hit){
            if (decay <= 0) {
                combo--;
                decay = getDecayRate();
            } else {
                decay--;
            }
        } else {
            hit = false;
        }
        if (turns > 0)
            turns--;
        return 100;
    }

    @Override
    public boolean isActive() {
        return combo > 0 || turns > 0;
    }

    @Override
    public int modifyAccuracy(int accuracy) {
        return accuracy + Math.min(combo * getLevel(), getAccuracyLimit());
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public boolean setLevel(int level) {
        this.level = level;
        return true;
    }

    @Override
    public void doOnHit(Entity self, Entity other, AttackInfo attackInfo) {
        if (other instanceof Animal && attackInfo.isHit()){
            if (combo < getAccuracyLimit())
                combo++;
            // TODO: roll into attack during combatmod rework
            // other.dealDamage((int)Math.log(combo), attackInfo.getDamageType());
            hit = true;
        } else {
            hit = false;
        }
    }

    @Override
    public String getName() {
        return String.join("",
            "Combo Breaker",
            combo > 0 ? " x" + combo : "",
            turns > 0 ? " (" + turns + ")" : ""
        );
    }

    @Override
    public void activate() {
        Display.getRootMenu().startSelecting((space) -> {
            if (space.isOccupied()) {
                for (int i = 0; i < Math.max(combo/3, 1); i++)
                    if (space.isOccupied()) {
                        Floor.doAttack(owner, space.getOccupant());
                    }
                combo = 0;
                turns = getCoolDown();
                Dungeon.update(owner.getTimeToAttack());
            }
            return true;
        });
    }

    @Override
    public boolean isEnabled() {
        return combo > 0 && turns <= 0;
    }

    private int getAccuracyLimit(){
        return 5 * getLevel();
    }

    private int getCoolDown(){
        return (int)(100/Math.pow(2,getLevel()-1));
    }
    
    private int getDecayRate(){
        return 1 + ((getLevel()-1) * 2);
    }

}
