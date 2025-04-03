package game.gameobjects.enchantments;

import java.util.HashSet;
import java.util.Set;

import org.hexworks.zircon.api.data.Tile;

import game.gamelogic.HasDrops;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHit;
import game.gameobjects.entities.Entity;

public class Lucky extends WeaponEnchantment implements OnHit{

    private int points;
    private Set<Entity> entities = new HashSet<Entity>();

    public Lucky(){
        this.points = 10;
        this.prefix = "Lucky";
    }

    @Override
    public void doOnHit(Entity self, Entity other, AttackInfo attackInfo) {
        if (other instanceof HasDrops hasDrops && entities.add(other)){
            hasDrops.addDropPoints(points);
        }
    }

    @Override
    public String getDescription() {
        return "This enchantment improves the user's luck, causing slain enemies to drop higher quality and more numerous goods.";
    }

    @Override
    public Tile getTile() {
        return Tile.empty();
    }

    @Override
    public String getName() {
        return "Lucky";
    }
}
