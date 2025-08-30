package game.floorgeneration.pools;

import game.gameobjects.entities.Entity;
import game.gameobjects.entities.props.Chest;
import game.gameobjects.items.Item;

public abstract class LayerPool {

    public final Pool<Entity> MONSTER_POOL;

    public final Pool<Item> DROP_POOL;

    public final Pool<Item> TREASURE_POOL;

    public final Pool<Chest> CHEST_POOL;

    public final Pool<Entity> BOSS_POOL;

    public final Pool<Entity> PROP_POOL;

    public final Pool<Item> PROP_DROP_POOL;

    public LayerPool(
        Pool<Entity> monsterPool,
        Pool<Item> dropPool,
        Pool<Item> treasurePool,
        Pool<Chest> chestPool,
        Pool<Entity> bossPool,
        Pool<Entity> propPool,
        Pool<Item> propDropPool
    ) {
        this.BOSS_POOL = bossPool;
        this.CHEST_POOL = chestPool;
        this.TREASURE_POOL = treasurePool;
        this.DROP_POOL = dropPool;
        this.MONSTER_POOL = monsterPool;
        this.PROP_POOL = propPool;
        this.PROP_DROP_POOL = propDropPool;
    }

}
