package game.floorgeneration;

import static game.App.randomNumber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import game.gameobjects.enchantments.ArmorEnchantment;
import game.gameobjects.enchantments.Clotting;
import game.gameobjects.enchantments.Flaming;
import game.gameobjects.enchantments.Lucky;
import game.gameobjects.enchantments.Thorny;
import game.gameobjects.enchantments.WeaponEnchantment;
import game.gameobjects.entities.Chest;
import game.gameobjects.entities.DireRat;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.Rat;
import game.gameobjects.entities.Slime;
import game.gameobjects.entities.Snake;
import game.gameobjects.entities.Spider;
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

public class Pools {

    public final static Pool<Supplier<Entity>> LAYER_ONE_MONSTER_POOL = new Pool<Supplier<Entity>>();

    public final static Pool<Supplier<Item>> LAYER_ONE_DROP_POOL = new Pool<Supplier<Item>>();

    public final static Pool<Supplier<Item>> LAYER_ONE_TREASURE_POOL = new Pool<Supplier<Item>>();
    
    public final static Pool<Supplier<Chest>> LAYER_ONE_CHEST_POOL = new Pool<Supplier<Chest>>();

    public final static Pool<Supplier<Entity>> LAYER_ONE_BOSS_POOL = new Pool<Supplier<Entity>>();

    public final static List<WeaponEnchantment> WEAPON_ENCHANTMENT_LIST= List.of(
        new Flaming(), new Lucky()
    );

    public final static List<ArmorEnchantment> ARMOR_ENCHANTMENT_LIST = List.of(
        new Thorny(), new Clotting()
    );
    
    public static void initialize(){
        
        Map<Supplier<Chest>, Integer> layerOneChestMap = new HashMap<Supplier<Chest>, Integer>();
        layerOneChestMap.put(()-> {return new Chest();}, 20);
        LAYER_ONE_CHEST_POOL.setMap(layerOneChestMap);
        
        Map<Supplier<Entity>, Integer> layerOneMonsterMap = new HashMap<Supplier<Entity>, Integer>();
        layerOneMonsterMap.put(()->{return new Rat();}, 5);
        layerOneMonsterMap.put(()->{return new DireRat();}, 6);
        layerOneMonsterMap.put(()->{return new Slime();}, 8);
        layerOneMonsterMap.put(()->{return new Snake();}, 7);
        layerOneMonsterMap.put(()->{return new Spider();}, 10);
        LAYER_ONE_MONSTER_POOL.setMap(layerOneMonsterMap);

        Map<Supplier<Item>, Integer> layerOneDropMap = new HashMap<Supplier<Item>, Integer>();
        layerOneDropMap.put(()->{return new BoStaff();}, 5);
        layerOneDropMap.put(()->{return new Dagger();}, 10);
        layerOneDropMap.put(()->{return new ShortSword();}, 5);
        layerOneDropMap.put(()->{return new HandAxe();}, 5);
        layerOneDropMap.put(()->{return new FirePotion();}, 10);
        layerOneDropMap.put(()->{return new HealingPotion();}, 15);
        layerOneDropMap.put(()->{return new LeatherCap();}, 5);
        layerOneDropMap.put(()->{return new LeatherArmor();}, 10);
        layerOneDropMap.put(()->{return new LeatherGloves();}, 5);
        layerOneDropMap.put(()->{return new LeatherPants();}, 5);
        layerOneDropMap.put(()->{return new Cloak();}, 5);
        layerOneDropMap.put(()->{return new Torch();}, 3);
        LAYER_ONE_DROP_POOL.setMap(layerOneDropMap);

        Map<Supplier<Item>, Integer> layerOneTreasureMap = new HashMap<Supplier<Item>, Integer>();
        layerOneTreasureMap.put(()->{return new Chainmail();}, 7);
        layerOneTreasureMap.put(()->{return new FirePotion();}, 5);
        layerOneTreasureMap.put(()->{return new HealingPotion();}, 5);
        layerOneTreasureMap.put(()->{return new IronGreaves();}, 7);
        layerOneTreasureMap.put(()->{return new IronHelm();}, 7);
        layerOneTreasureMap.put(()->{return new PlateGauntlets();}, 7);
        layerOneTreasureMap.put(()->{return new ScrollOfUpgrade();}, 10);
        layerOneTreasureMap.put(()->{return new ScrollOfEnchantment();}, 10);
        layerOneTreasureMap.put(()->{return new PlateArmor();}, 10);
        layerOneTreasureMap.put(()->{return new BoStaff();}, 5);
        layerOneTreasureMap.put(()->{return new Dagger();}, 10);
        layerOneTreasureMap.put(()->{return new ShortSword();}, 5);
        layerOneTreasureMap.put(()->{return new HandAxe();}, 5);
        layerOneTreasureMap.put(()->{return new Torch();}, 3);
        LAYER_ONE_TREASURE_POOL.setMap(layerOneTreasureMap);

        Map<Supplier<Entity>, Integer> layerOneBossMap = new HashMap<Supplier<Entity>, Integer>();
        layerOneBossMap.put(()->{return new RatKing();}, 1);
        LAYER_ONE_BOSS_POOL.setMap(layerOneBossMap);

    }

    public static <T> T getRandom(List<T> list){
        return list.get(randomNumber(0,list.size()-1));
    }
}
