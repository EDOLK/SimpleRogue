package game.gameobjects.enchantments;

import static game.App.randomNumber;

import org.hexworks.zircon.api.data.Tile;

import game.floorgeneration.Shopper;
import game.gamelogic.Attribute;
import game.gamelogic.HasDrops;
import game.gamelogic.combat.Attack;
import game.gamelogic.combat.AttackModifier;
import game.gameobjects.items.Item;

public class Lucky extends WeaponEnchantment implements AttackModifier{

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
    public void modifyAttack(Attack attack) {
        attack.attachPostAttackHook((ar) -> {
            if (attack.getWeapon().getEnchantment() == this && ar.hit() && !ar.defender().isAlive()) {

                int lowerBound = 0;
                int upperBound = 0;

                if (ar.defender() instanceof HasDrops hasDrops) {
                    upperBound = hasDrops.getDropPoints();
                    upperBound *= randomNumber(0d, 1d + (Attribute.getAttribute(Attribute.LUCK, ar.attacker())/10d));
                    Shopper<Item> shopper = new Shopper<Item>(
                        randomNumber(lowerBound, upperBound),
                        hasDrops.getItemPool()
                    );
                    while (shopper.hasPoints()) {
                        Item generated = shopper.generate();
                        if (generated != null) {
                            ar.defender().getSpace().addItem(generated);
                        }
                    }
                }
            }
        });
    }

}
