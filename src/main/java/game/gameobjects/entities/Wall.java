package game.gameobjects.entities;

import java.util.ArrayList;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.HasResistances;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Mossy;
import game.gameobjects.statuses.Status;

public class Wall extends Entity implements HasResistances{
    
    private ArrayList<Resistance> resistances = new ArrayList<Resistance>();

    public Wall(){
        super(TileColor.transparent(), TileColor.create(200, 200, 200, 255), '█');
        setBaseMaxHP(100);
        setHP(100);
        setName("Wall");
        setTileName("Wall");
        setDescription("it's... a wall.");
        setSightBlocker(true);
        setGasBlocker(true);
        setLiquidBlocker(true);
        setLightBlocker(true);
        setWeight(100);
        for (DamageType damageType : DamageType.values()) {
            if (damageType == DamageType.PIERCING){
                resistances.add(new PercentageResistance(damageType, 0.90));
            } else {
                resistances.add(new PercentageResistance(damageType, 1.00));
            }
        }
    }

    @Override
    public Item getCorpse() {
        Item i = new Item(this.getBgColor().darkenByPercent(.50), this.getFgColor().darkenByPercent(.50), '░');
        i.setName("Rubble");
        i.setDescription("A pile of rubble.");
        i.setWeight(500);
        return i;
    }

    @Override
    public int defaultInteraction(Entity entity) {
        return entity.getTimeToWait();
    }

    @Override
    public String getDeathMessage() {
        return "The " + getName() + " collapses into a pile of rubble.";
    }

    @Override
    public ArrayList<Resistance> getResistances() {
        return resistances;
    }

    @Override
    protected boolean baseVulnerable(Status status) {
        return status instanceof Mossy;
    }

}
