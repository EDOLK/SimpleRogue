package game.gameobjects.enchantments;

import static game.App.randomNumber;

import org.hexworks.zircon.api.data.Tile;

import game.gamelogic.combat.Attack;
import game.gamelogic.combat.AttackModifier;
import game.gameobjects.statuses.Burning;
import game.gameobjects.terrains.Fire;

public class Flaming extends WeaponEnchantment implements AttackModifier{
    
    public Flaming(){
        this.prefix = "Flaming";
    }

    @Override
    public String getDescription() {
        return "This enchantment imbues the user's weapon with igneous energy, giving it a small chance to light enemies on fire.";
    }

    @Override
    public Tile getTile() {
        return new Fire(1).getTile();
    }

    @Override
    public String getName() {
        return "Flaming";
    }

    @Override
    public void modifyAttack(Attack attack) {
        attack.attachPostAttackHook((attackResult) -> {
            if (attack.getWeapon().getEnchantment() == this && attackResult.hit() && randomNumber(1, 4) == 4)
                attackResult.defender().addStatus(new Burning());
        });
    }
    
}
