package game.gameobjects.items.weapons;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Flammable;
import game.gamelogic.SelfAware;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.terrains.Fire;

public class BoStaff extends Weapon implements SelfAware, Flammable{

    private Space space;

    public BoStaff(){
        super(TileColor.transparent(), TileColor.create(140, 63, 38, 255), 'b');
        setName("Bo Staff");
        setTileName("Staff");
        setDescription("A wooden Bo staff. Accurate and highly flammable.");
        setMinDamage(1);
        setMaxDamage(4);
        setWeight(5);
        setAccuracy(3);
        setDamageType(DamageType.BLUNT);
    }

    @Override
    public int getFuelValue() {
        return getWeight();
    }

    @Override
    public void onBurn(Fire fire) {
        getSpace().getItems().remove(this);
    }

    @Override
    public Space getSpace() {
        return space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }
    
}
