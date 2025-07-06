package game.gameobjects.enchantments;

import static game.App.randomNumber;

import org.hexworks.zircon.api.data.Tile;

import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHitted;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Bleeding;

public class Thorny extends ArmorEnchantment implements OnHitted {

    public Thorny(){
        this.prefix = "Thorny";
    }

    @Override
    public void doOnHitted(Entity self, Entity other, AttackInfo attackInfo) {
        if (randomNumber(1,5) == 5 && attackInfo.getDamageDelt() != 0){
            other.addStatus(new Bleeding((int)(attackInfo.getDamageDelt()*0.5),1,3));
        }
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
