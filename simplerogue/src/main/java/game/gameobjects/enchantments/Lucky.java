package game.gameobjects.enchantments;

import java.util.HashSet;
import java.util.Set;

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
            System.out.println(hasDrops.getDropPoints());
            hasDrops.addDropPoints(points);
            System.out.println(hasDrops.getDropPoints());
        }
    }
}
