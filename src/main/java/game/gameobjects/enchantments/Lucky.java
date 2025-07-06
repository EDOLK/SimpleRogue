package game.gameobjects.enchantments;

import static game.App.randomNumber;

import org.hexworks.zircon.api.data.Tile;

import game.floorgeneration.Shopper;
import game.gamelogic.HasAttributes;
import game.gamelogic.HasDrops;
import game.gamelogic.AttributeMap.Attribute;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnKill;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;

public class Lucky extends WeaponEnchantment implements OnKill{

    public Lucky(){
        this.prefix = "Lucky";
    }

    @Override
    public String getDescription() {
        return "This enchantment improves the user's luck, causing slain enemies to drop higher quality and more numerous loot.";
    }

    @Override
    public Tile getTile() {
        return Tile.empty();
    }

    @Override
    public String getName() {
        return "Lucky";
    }

    @Override
    public void doOnKill(Entity self, Entity other, AttackInfo attackInfo) {

        int lowerBound = 0;
        int upperBound = 0;

        if (other instanceof HasDrops hasDrops) {
            upperBound = hasDrops.getDropPoints();
            if (self instanceof HasAttributes hasAttributes) {
                upperBound *= randomNumber(0d, 1d + (hasAttributes.getAttribute(Attribute.LUCK)/10d));
            } else {
                upperBound *= randomNumber(0d, 1d);
            }
            Shopper<Item> shopper = new Shopper<Item>(
                randomNumber(lowerBound, upperBound),
                hasDrops.getItemPool()
            );
            while (shopper.hasPoints()) {
                Item generated = shopper.generate();
                if (generated != null) {
                    other.getSpace().addItem(generated);
                }
            }
        }

    }

}
