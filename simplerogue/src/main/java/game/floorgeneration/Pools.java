package game.floorgeneration;

import static game.App.randomNumber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.floorgeneration.ItemFactory.ItemIdentifier;
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

public class Pools {

    public final static Pool<Class<? extends Entity>> LAYER_ONE_MONSTER_POOL = new Pool<Class<? extends Entity>>();

    public final static Pool<ItemIdentifier> LAYER_ONE_DROP_POOL = new Pool<ItemIdentifier>();

    public final static Pool<ItemIdentifier> LAYER_ONE_TREASURE_POOL = new Pool<ItemIdentifier>();
    
    public final static Pool<Class<? extends Entity>> LAYER_ONE_CHEST_POOL = new Pool<Class<? extends Entity>>();

    public final static List<WeaponEnchantment> WEAPON_ENCHANTMENT_LIST= List.of(
        new Flaming(), new Lucky()
    );

    public final static List<ArmorEnchantment> ARMOR_ENCHANTMENT_LIST = List.of(
        new Thorny(), new Clotting()
    );
    
    public static void initialize(){
        
        Map<Class<? extends Entity>, Integer> layerOneChestMap = new HashMap<Class<? extends Entity>, Integer>();
        layerOneChestMap.put(Chest.class, 20);
        LAYER_ONE_CHEST_POOL.setMap(layerOneChestMap);
        
        Map<Class<? extends Entity>, Integer> layerOneMonsterMap = new HashMap<Class<? extends Entity>, Integer>();
        layerOneMonsterMap.put(Rat.class, 5);
        layerOneMonsterMap.put(DireRat.class, 6);
        layerOneMonsterMap.put(Slime.class, 8);
        layerOneMonsterMap.put(Snake.class, 7);
        layerOneMonsterMap.put(Spider.class, 10);
        LAYER_ONE_MONSTER_POOL.setMap(layerOneMonsterMap);

        Map<ItemIdentifier, Integer> layerOneDropMap = new HashMap<ItemIdentifier, Integer>();
        layerOneDropMap.put(ItemIdentifier.BO_STAFF, 5);
        layerOneDropMap.put(ItemIdentifier.DAGGER, 10);
        layerOneDropMap.put(ItemIdentifier.SHORTSWORD, 5);
        layerOneDropMap.put(ItemIdentifier.HANDAXE, 5);
        layerOneDropMap.put(ItemIdentifier.FIRE_POTION, 10);
        layerOneDropMap.put(ItemIdentifier.HEALING_POTION, 15);
        layerOneDropMap.put(ItemIdentifier.LEATHER_CAP, 5);
        layerOneDropMap.put(ItemIdentifier.LEATHER_ARMOR, 10);
        layerOneDropMap.put(ItemIdentifier.LEATHER_GLOVES, 5);
        layerOneDropMap.put(ItemIdentifier.LEATHER_PANTS, 5);
        layerOneDropMap.put(ItemIdentifier.CLOAK, 5);
        layerOneDropMap.put(ItemIdentifier.TORCH, 3);
        LAYER_ONE_DROP_POOL.setMap(layerOneDropMap);

        Map<ItemIdentifier, Integer> layerOneTreasureMap = new HashMap<ItemIdentifier, Integer>();
        layerOneTreasureMap.put(ItemIdentifier.CHAINMAIL, 7);
        layerOneTreasureMap.put(ItemIdentifier.FIRE_POTION, 5);
        layerOneTreasureMap.put(ItemIdentifier.HEALING_POTION, 5);
        layerOneTreasureMap.put(ItemIdentifier.IRON_GREAVES, 7);
        layerOneTreasureMap.put(ItemIdentifier.IRON_HELM, 7);
        layerOneTreasureMap.put(ItemIdentifier.PLATE_GAUNTLETS, 7);
        layerOneTreasureMap.put(ItemIdentifier.SCROLL_OF_UPGRADE, 10);
        layerOneTreasureMap.put(ItemIdentifier.SCROLL_OF_ENCHANTMENT, 10);
        layerOneTreasureMap.put(ItemIdentifier.PLATE_ARMOR, 10);
        layerOneTreasureMap.put(ItemIdentifier.BO_STAFF, 5);
        layerOneTreasureMap.put(ItemIdentifier.DAGGER, 10);
        layerOneTreasureMap.put(ItemIdentifier.SHORTSWORD, 5);
        layerOneTreasureMap.put(ItemIdentifier.HANDAXE, 5);
        layerOneTreasureMap.put(ItemIdentifier.TORCH, 3);
        LAYER_ONE_TREASURE_POOL.setMap(layerOneTreasureMap);


    }

    public static <T> T getRandom(List<T> list){
        return list.get(randomNumber(0,list.size()-1));
    }
}
