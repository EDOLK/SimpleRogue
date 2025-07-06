package game.gameobjects.enchantments;

import static game.App.randomNumber;

import org.hexworks.zircon.api.data.Tile;

import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHit;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Burning;
import game.gameobjects.terrains.Fire;

public class Flaming extends WeaponEnchantment implements OnHit{
    
    public Flaming(){
        this.prefix = "Flaming";
    }

    @Override
    public void doOnHit(Entity self, Entity other, AttackInfo attackInfo) {
        if (randomNumber(1, 4) == 4){
            other.addStatus(new Burning());
        }
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
    
}
