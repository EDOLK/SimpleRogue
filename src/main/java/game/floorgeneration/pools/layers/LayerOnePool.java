package game.floorgeneration.pools.layers;

import java.util.function.Supplier;

import game.floorgeneration.pools.LayerPool;
import game.floorgeneration.pools.PoolBuilder;
import game.gameobjects.entities.DireRat;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.Rat;
import game.gameobjects.entities.Slime;
import game.gameobjects.entities.Snake;
import game.gameobjects.entities.Spider;
import game.gameobjects.entities.bosses.Ghast;
import game.gameobjects.entities.bosses.RatKing;
import game.gameobjects.entities.props.Barrel;
import game.gameobjects.entities.props.Brazier;
import game.gameobjects.entities.props.Chest;
import game.gameobjects.entities.props.Crate;
import game.gameobjects.items.Antidote;
import game.gameobjects.items.Bandage;
import game.gameobjects.items.Item;
import game.gameobjects.items.Ration;
import game.gameobjects.items.Torch;
import game.gameobjects.items.armor.Chainmail;
import game.gameobjects.items.armor.Cloak;
import game.gameobjects.items.armor.IronGreaves;
import game.gameobjects.items.armor.IronHelm;
import game.gameobjects.items.armor.LeatherArmor;
import game.gameobjects.items.armor.LeatherCap;
import game.gameobjects.items.armor.LeatherGloves;
import game.gameobjects.items.armor.LeatherPants;
import game.gameobjects.items.armor.PlateArmor;
import game.gameobjects.items.armor.PlateGauntlets;
import game.gameobjects.items.potions.FirePotion;
import game.gameobjects.items.potions.HealingPotion;
import game.gameobjects.items.scrolls.ScrollOfEnchantment;
import game.gameobjects.items.scrolls.ScrollOfUpgrade;
import game.gameobjects.items.weapons.BoStaff;
import game.gameobjects.items.weapons.Dagger;
import game.gameobjects.items.weapons.HandAxe;
import game.gameobjects.items.weapons.ShortSword;

public class LayerOnePool extends LayerPool {

    public LayerOnePool(){
        super(

            new PoolBuilder<Supplier<Entity>>()
            .put(()->new Rat(), 5)
            .put(()->new DireRat(), 6)
            .put(()->new Slime(), 8)
            .put(()->new Snake(), 7)
            .put(()->new Spider(), 10)
            .build(),

            new PoolBuilder<Supplier<Item>>()
            .put(()->new BoStaff(), 5)
            .put(()->new Dagger(), 10)
            .put(()->new ShortSword(), 5)
            .put(()->new HandAxe(), 5)
            .put(()->new FirePotion(), 10)
            .put(()->new HealingPotion(), 15)
            .put(()->new LeatherCap(), 5)
            .put(()->new LeatherArmor(), 10)
            .put(()->new LeatherGloves(), 5)
            .put(()->new LeatherPants(), 5)
            .put(()->new Cloak(), 5)
            .build(),

            new PoolBuilder<Supplier<Item>>()
            .put(()->new Chainmail(), 7)
            .put(()->new FirePotion(), 5)
            .put(()->new HealingPotion(), 5)
            .put(()->new IronGreaves(), 7)
            .put(()->new IronHelm(), 7)
            .put(()->new PlateGauntlets(), 7)
            .put(()->new ScrollOfUpgrade(), 10)
            .put(()->new ScrollOfEnchantment(), 10)
            .put(()->new PlateArmor(), 10)
            .put(()->new BoStaff(), 5)
            .put(()->new Dagger(), 10)
            .put(()->new ShortSword(), 5)
            .put(()->new HandAxe(), 5)
            .build(),

            new PoolBuilder<Supplier<Chest>>()
            .put(()->new Chest(), 20)
            .build(),

            new PoolBuilder<Supplier<Entity>>()
            .put(()->new RatKing(), 1)
            .put(()->new Ghast(), 1)
            .build(),

            new PoolBuilder<Supplier<Entity>>()
            .put(()->new Crate(), 2)
            .put(()->new Barrel(), 2)
            .put(()->new Brazier(Math.random() <= 0.50), 2)
            .build(),

            new PoolBuilder<Supplier<Item>>()
            .put(()->new Torch(), 3)
            .put(()->new Antidote(), 3)
            .put(()->new Ration(), 2)
            .put(()->new Bandage(), 2)
            .build()

        );
    }

}
