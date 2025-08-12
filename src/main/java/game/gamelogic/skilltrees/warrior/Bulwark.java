package game.gamelogic.skilltrees.warrior;

import game.display.Display;
import game.gamelogic.Levelable;
import game.gamelogic.abilities.Ability;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHitted;
import game.gameobjects.AttackResult;
import game.gameobjects.DamageType;
import game.gameobjects.Floor;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.weapons.Weapon;

public class Bulwark implements Ability, OnHitted, Behavable, Levelable {

    private int turnsLeftInCooldown;
    private int turnsLeftForDecay;
    private int accumulatedDamage;
    private int level = 1;
    private Entity owner;

    public Bulwark(Entity owner) {
        this.owner = owner;
    }

    private int getDamageLimit(){
        return 10 * getLevel();
    }

    private int getCooldown(){
        return 100/(int)Math.pow(2,getLevel()-1);
    }

    private int getDecayRate(){
        return 5 * getLevel();
    }

    @Override
    public String getName() {
        return String.join(
            "",
            "Bulwark",
            (accumulatedDamage > 0 ? " x" + accumulatedDamage : ""),
            (turnsLeftInCooldown > 0 ? " (" + turnsLeftInCooldown + ")" : "")
        );
    }

    @Override
    public void activate() {
        Display.getRootMenu().startSelecting((space) -> {
            if (space.isOccupied()) {
                Weapon bulwarkWeapon = new Weapon();
                bulwarkWeapon.setDamage(accumulatedDamage, accumulatedDamage);
                bulwarkWeapon.setDamageType(DamageType.BLUNT);
                AttackResult result = Floor.doAttack(owner, space.getOccupant(), bulwarkWeapon);
                if (result.hit())
                    accumulatedDamage = 0;
                turnsLeftInCooldown = getCooldown();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean isEnabled() {
        return turnsLeftInCooldown <= 0 && accumulatedDamage > 0;
    }

    @Override
    public void doOnHitted(Entity self, Entity other, AttackInfo attackInfo) {
        if (accumulatedDamage < getDamageLimit() && turnsLeftInCooldown <= 0)
            accumulatedDamage += Math.min(Math.abs(attackInfo.getDamage() - attackInfo.getDamageDelt()), Math.abs(accumulatedDamage - getDamageLimit()));
    }

    @Override
    public int behave() {

        if (turnsLeftInCooldown > 0)
            turnsLeftInCooldown--;

        if (turnsLeftForDecay > 0){
            turnsLeftForDecay--;
        } else {
            if (accumulatedDamage > 0)
                accumulatedDamage--;
            turnsLeftForDecay = getDecayRate();
        }

        return 100;
    }

    @Override
    public boolean isActive() {
        return turnsLeftInCooldown > 0 || accumulatedDamage > 0;
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
}
