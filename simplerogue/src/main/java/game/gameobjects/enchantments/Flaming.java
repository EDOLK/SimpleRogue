package game.gameobjects.enchantments;

import static game.App.randomNumber;

import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHit;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Burning;

public class Flaming extends WeaponEnchantment implements OnHit{
    
    public Flaming(){
        this.prefix = "Flaming";
    }

    @Override
    public void activate(Entity self, Entity other, AttackInfo attackInfo) {
        if (randomNumber(1, 4) == 4){
            Burning burning = (Burning)other.getStatusByClass(Burning.class);
            if (burning != null){
                burning.addTurns(2);
            } else {
                other.addStatus(new Burning());
            }
        }
    }
    
}
