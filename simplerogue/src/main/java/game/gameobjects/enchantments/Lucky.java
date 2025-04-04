package game.gameobjects.enchantments;

import static game.App.randomNumber;

import org.hexworks.zircon.api.data.Tile;

import game.display.Display;
import game.floorgeneration.Shopper;
import game.gamelogic.HasDrops;
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

        if (other instanceof HasDrops hasDrops) {
            double d = randomNumber(0,1.5);
            Shopper<Item> shopper = new Shopper<Item>(
                (int)Math.floor(hasDrops.getDropPoints() * d),
                hasDrops.getItemPool()
            );
            Display.log(Double.toString(d));
            while (shopper.hasPoints()) {
                Item generated = shopper.generate();
                if (generated != null) {
                    Display.log(generated.getName(), other.getSpace());
                    other.getSpace().addItem(generated);
                }
            }
        }

    }

}
