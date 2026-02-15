package game.gameobjects.entities.props;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.gamelogic.DamageModifier;
import game.gamelogic.HasDamageModifiers;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Mossy;
import game.gameobjects.statuses.Status;

public class Statue extends Entity implements HasDamageModifiers {

    private List<Resistance> resistances;

    public Statue() {
        this(Math.random() < 0.10 ? 
            Dungeon.getCurrentBossPool().getRandom(Dungeon.getCurrentBossPool().getHighestPrice()).get() : 
            Dungeon.getCurrentMonsterPool().getRandom(Dungeon.getCurrentMonsterPool().getHighestPrice()).get() 
        );
    }

    public Statue(Entity referenceEntity) {
        this.resistances = new ArrayList<>();
        for (DamageType type : DamageType.values()) {
            if (type != DamageType.BLUNT && type != DamageType.PIERCING && type != DamageType.SLASHING) {
                resistances.add(new PercentageResistance(type, 1.0));
            }
        }
        if (referenceEntity != null) {
            this.setCharacter(referenceEntity.getCharacter());
            this.setFgColor(TileColor.create(200,200,200,255));
            this.setBgColor(TileColor.transparent());
            this.setName(referenceEntity.getOriginalName() + " Statue");
            this.setDescription("A stone statue of a " + referenceEntity.getOriginalName() + ".");
        }
        this.setWeight(20);
        this.setBaseMaxHP(15);
        this.setHP(15);
    }

    @Override
    public Item getCorpse() {
        Item i = new Item(this.getBgColor().darkenByPercent(.50), this.getFgColor().darkenByPercent(.50), '░');
        i.setName("Rubble");
        i.setDescription("A pile of rubble.");
        i.setWeight(20);
        return i;
    }

    @Override
    public int defaultInteraction(Entity interactor) {
        return interactor.getTimeToWait();
    }

    @Override
    public String getDeathMessage() {
        return "The " + getName() + " collapses into a pile of rubble.";
    }

    @Override
    protected boolean baseVulnerable(Status status) {
        return status instanceof Mossy;
    }

    @Override
    public List<DamageModifier> getDamageModifiers() {
        return List.copyOf(this.resistances);
    }

}
