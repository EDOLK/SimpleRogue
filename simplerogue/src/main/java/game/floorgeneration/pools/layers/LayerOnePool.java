package game.floorgeneration.pools.layers;

import java.util.function.Supplier;

import game.floorgeneration.pools.LayerPool;
import game.floorgeneration.pools.PoolBuilder;
import game.gameobjects.entities.Chest;
import game.gameobjects.entities.DireRat;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.Rat;
import game.gameobjects.entities.Slime;
import game.gameobjects.entities.Snake;
import game.gameobjects.entities.Spider;
import game.gameobjects.entities.bosses.Ghast;
import game.gameobjects.entities.bosses.RatKing;
import game.gameobjects.items.Item;
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
            .put(()->{return new Rat();}, 5)
            .put(()->{return new DireRat();}, 6)
            .put(()->{return new Slime();}, 8)
            .put(()->{return new Snake();}, 7)
            .put(()->{return new Spider();}, 10)
            .build(),

            new PoolBuilder<Supplier<Item>>()
            .put(()->{return new BoStaff();}, 5)
            .put(()->{return new Dagger();}, 10)
            .put(()->{return new ShortSword();}, 5)
            .put(()->{return new HandAxe();}, 5)
            .put(()->{return new FirePotion();}, 10)
            .put(()->{return new HealingPotion();}, 15)
            .put(()->{return new LeatherCap();}, 5)
            .put(()->{return new LeatherArmor();}, 10)
            .put(()->{return new LeatherGloves();}, 5)
            .put(()->{return new LeatherPants();}, 5)
            .put(()->{return new Cloak();}, 5)
            .put(()->{return new Torch();}, 3)
            .build(),

            new PoolBuilder<Supplier<Item>>()
            .put(()->{return new Chainmail();}, 7)
            .put(()->{return new FirePotion();}, 5)
            .put(()->{return new HealingPotion();}, 5)
            .put(()->{return new IronGreaves();}, 7)
            .put(()->{return new IronHelm();}, 7)
            .put(()->{return new PlateGauntlets();}, 7)
            .put(()->{return new ScrollOfUpgrade();}, 10)
            .put(()->{return new ScrollOfEnchantment();}, 10)
            .put(()->{return new PlateArmor();}, 10)
            .put(()->{return new BoStaff();}, 5)
            .put(()->{return new Dagger();}, 10)
            .put(()->{return new ShortSword();}, 5)
            .put(()->{return new HandAxe();}, 5)
            .put(()->{return new Torch();}, 3)
            .build(),

            new PoolBuilder<Supplier<Chest>>()
            .put(()-> {return new Chest();}, 20)
            .build(),

            new PoolBuilder<Supplier<Entity>>()
            .put(()->{return new RatKing();}, 1)
            .put(()->{return new Ghast();}, 1)
            .build()

        );
    }

}
