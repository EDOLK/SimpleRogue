package game.gamelogic.skilltrees.rogue;

import game.App;
import game.display.Display;
import game.gamelogic.Levelable;
import game.gamelogic.Skill;
import game.gamelogic.abilities.Passive;
import game.gamelogic.combat.Attack;
import game.gamelogic.combat.AttackModifier;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;

public class SneakAttack implements Passive, AttackModifier, Levelable{

    private int level = 1;
    private Entity owner;

    public SneakAttack(Entity owner){
        this.owner = owner;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean setLevel(int level) {
        if (level > 3 || level < 1)
            return false;
        this.level = level;
        return true;
    }

    @Override
    public void modifyAttack(Attack attack) {
        if (attack.getAttacker() == owner && attack.isSneak()) {
            int stealth = Skill.getSkill(Skill.STEALTH, owner);
            switch (level) {
                case 1:
                    attack.attachAccuracyModifier((a) -> (a + App.randomNumber(0, stealth)), Attack.BASE_PRIORITY+1);
                    attack.attachDamageModifier((d, dt) -> (attack.getWeapon().getMaxDamage()), Attack.BASE_PRIORITY+1);
                    break;
                case 2:
                    attack.attachAccuracyModifier((a) -> (a + stealth), Attack.BASE_PRIORITY+1);
                    attack.attachAccuracyModifier((a) -> a*2, Attack.BASE_PRIORITY+2);
                    attack.attachDamageModifier((d, dt) -> (attack.getWeapon().getMaxDamage()), Attack.BASE_PRIORITY+1);
                    attack.attachDamageModifier((d, dt) -> (d + App.randomNumber(0, stealth)), Attack.BASE_PRIORITY+2);
                    break;
                case 3:
                    attack.attachAccuracyModifier((a) -> (999), Attack.BASE_PRIORITY+1);
                    attack.attachDamageModifier((d, dt) -> (attack.getWeapon().getMaxDamage()), Attack.BASE_PRIORITY+1);
                    attack.attachDamageModifier((d, dt) -> (d + stealth), Attack.BASE_PRIORITY+2);
                    attack.attachDamageModifier((d, dt) -> (int)(d*2), Attack.BASE_PRIORITY+3);
                    break;
                default:
                    break;
            }
            attack.attachPostAttackHook((attackResult) -> {
                if (attack.getAttacker() instanceof PlayerEntity)
                    Display.logHeader("Sneak Attack!");
            });
        }
    }

}
