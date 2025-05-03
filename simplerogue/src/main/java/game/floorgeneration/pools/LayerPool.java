package game.floorgeneration.pools;

import java.util.function.Supplier;

import game.gameobjects.entities.Chest;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;

public abstract class LayerPool {

    public final Pool<Supplier<Entity>> MONSTER_POOL;

    public final Pool<Supplier<Item>> DROP_POOL;

    public final Pool<Supplier<Item>> TREASURE_POOL;

    public final Pool<Supplier<Chest>> CHEST_POOL;

    public final Pool<Supplier<Entity>> BOSS_POOL;

    public LayerPool(
        Pool<Supplier<Entity>> monsterPool,
        Pool<Supplier<Item>> dropPool,
        Pool<Supplier<Item>> treasurePool,
        Pool<Supplier<Chest>> chestPool,
        Pool<Supplier<Entity>> bossPool
    ) {
        this.BOSS_POOL = bossPool;
        this.CHEST_POOL = chestPool;
        this.TREASURE_POOL = treasurePool;
        this.DROP_POOL = dropPool;
        this.MONSTER_POOL = monsterPool;
    }

}
