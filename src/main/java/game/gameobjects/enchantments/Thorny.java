package game.gameobjects.enchantments;

import static game.App.randomNumber;

import org.hexworks.zircon.api.data.Tile;

import game.gamelogic.Armored;
import game.gamelogic.combat.Attack;
import game.gamelogic.combat.AttackModifier;
import game.gameobjects.statuses.Bleeding;

public class Thorny extends ArmorEnchantment implements AttackModifier {

    public Thorny(){
        this.prefix = "Thorny";
    }

    @Override
    public void modifyAttack(Attack attack) {
        attack.attachPostAttackHook((ar) -> {
            if (ar.defender() instanceof Armored armored && armored.getArmor().stream().anyMatch(a -> a.getEnchantment() == this) && ar.hit() && randomNumber(1, 5) == 5 && ar.damageDelt() != 0) {
                ar.attacker().addStatus(new Bleeding((int)(ar.damageDelt()*0.5),1,3));
            }
        });
    }

    @Override
    public String getDescription() {
        return "This enchantment covers the user's armor with aetherial spikes. When attacked, these spikes have a chance of manifesting and causing the attacking enemy to bleed for a fraction of the damage delt.";
    }

    @Override
    public Tile getTile() {
        return Tile.empty();
    }

    @Override
    public String getName() {
        return "Thorny";
    }
    
}
