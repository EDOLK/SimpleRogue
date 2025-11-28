package game.gamelogic.skilltrees.rogue;

import game.gamelogic.Levelable;
import game.gamelogic.abilities.Passive;
import game.gamelogic.combat.Attack;
import game.gamelogic.combat.AttackModifier;
import game.gameobjects.entities.Entity;

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
        if (level >= 4) {
            return false;
        }
        this.level = level;
        return true;
    }

    @Override
    public void modifyAttack(Attack attack) {
        if (attack.getAttacker() == owner && attack.isSneak()) {
            switch (level) {
                case 1:
                    attack.attachAccuracyModifier((a) -> (a*2), Attack.BASE_PRIORITY+1);
                    break;
                case 2:
                    attack.attachAccuracyModifier((a) -> (a*3), Attack.BASE_PRIORITY+2);
                    attack.attachDamageModifier((d, dt) -> (int)(d*2), Attack.BASE_PRIORITY+2);
                    break;
                case 3:
                    attack.attachAccuracyModifier((a) -> (999), Attack.BASE_PRIORITY+3);
                    attack.attachDamageModifier((d, dt) -> (d*3), Attack.BASE_PRIORITY+3);
                    break;
                default:
                    break;
            }
        }
    }

}
