package game.floorgeneration.pools.layers;

import game.floorgeneration.pools.ConcretePool;
import game.floorgeneration.pools.LayerPool;
import game.floorgeneration.pools.PoolEntry;
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
import game.gameobjects.entities.props.Statue;
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
import game.gameobjects.items.potions.FreezingPotion;
import game.gameobjects.items.potions.HealingPotion;
import game.gameobjects.items.potions.WaterPotion;
import game.gameobjects.items.scrolls.ScrollOfEnchantment;
import game.gameobjects.items.scrolls.ScrollOfUpgrade;
import game.gameobjects.items.weapons.BoStaff;
import game.gameobjects.items.weapons.Dagger;
import game.gameobjects.items.weapons.HandAxe;
import game.gameobjects.items.weapons.ShortSword;

public class LayerOnePool extends LayerPool {

    public LayerOnePool(){
        super(
            new ConcretePool.Builder<Entity>()
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new Rat())
                        .withPrice(5)
                        .withWeight(2.0d)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new DireRat())
                        .withPrice(6)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new Snake())
                        .withPrice(7)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new Slime())
                        .withPrice(8)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new Spider())
                        .withPrice(10)
                        .build()
                )
                .build(),

            new ConcretePool.Builder<Item>()
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new BoStaff())
                        .withPrice(5)
                        .withAmount(1)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new Dagger())
                        .withPrice(10)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new ShortSword())
                        .withPrice(5)
                        .withAmount(1)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new HandAxe())
                        .withPrice(5)
                        .withAmount(1)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new FirePotion())
                        .withPrice(10)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new FreezingPotion())
                        .withPrice(10)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new WaterPotion())
                        .withPrice(10)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new HealingPotion())
                        .withPrice(15)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new LeatherCap())
                        .withPrice(5)
                        .withAmount(1)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new LeatherArmor())
                        .withPrice(10)
                        .withAmount(1)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new LeatherGloves())
                        .withPrice(5)
                        .withAmount(1)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new LeatherPants())
                        .withPrice(5)
                        .withAmount(1)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new Cloak())
                        .withPrice(5)
                        .withAmount(1)
                        .build()
                )
                .build(),

            new ConcretePool.Builder<Item>()
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new Chainmail())
                        .withPrice(7)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new FirePotion())
                        .withPrice(5)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new WaterPotion())
                        .withPrice(5)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new FreezingPotion())
                        .withPrice(5)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new HealingPotion())
                        .withPrice(5)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new IronGreaves())
                        .withPrice(7)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new IronHelm())
                        .withPrice(7)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new PlateGauntlets())
                        .withPrice(7)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new ScrollOfUpgrade())
                        .withPrice(10)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new ScrollOfEnchantment())
                        .withPrice(10)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new PlateArmor())
                        .withPrice(10)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new BoStaff())
                        .withPrice(5)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new Dagger())
                        .withPrice(10)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new ShortSword())
                        .withPrice(5)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new HandAxe())
                        .withPrice(5)
                        .build()
                )
                .build(),

            new ConcretePool.Builder<Chest>()
                .put(
                    new PoolEntry.Builder<Chest>()
                        .with(()->new Chest())
                        .withPrice(20)
                        .build()
                )
                .build(),

            new ConcretePool.Builder<Entity>()
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new RatKing())
                        .withPrice(1)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new Ghast())
                        .withPrice(1)
                        .build()
                )
                .build(),

            new ConcretePool.Builder<Entity>()
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new Crate())
                        .withPrice(2)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new Barrel())
                        .withPrice(2)
                        .build()
                )
                .build(),

            new ConcretePool.Builder<Entity>()
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new Brazier(Math.random() <= 0.50))
                        .withAmount(1)
                        .withPrice(1)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Entity>()
                        .with(()->new Statue())
                        .withAmount(1)
                        .withPrice(1)
                        .build()
                )
                .build(),

            new ConcretePool.Builder<Item>()
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new Torch())
                        .withPrice(3)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new Antidote())
                        .withPrice(3)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new WaterPotion())
                        .withPrice(3)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new Ration())
                        .withPrice(2)
                        .build()
                )
                .put(
                    new PoolEntry.Builder<Item>()
                        .with(()->new Bandage())
                        .withPrice(2)
                        .build()
                )
                .build()
        );
    }
}
