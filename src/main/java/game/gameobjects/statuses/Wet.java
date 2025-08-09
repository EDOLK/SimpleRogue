package game.gameobjects.statuses;

import java.util.HashSet;
import java.util.List;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.behavior.Behavable;
import game.gameobjects.entities.Entity;
import game.gameobjects.terrains.liquids.Liquid;

public class Wet extends Status implements Behavable, FiltersOut, FiltersIn{

    private int timer = 20;
    private Liquid liquid;

    public Wet(Liquid liquid) {
        super();
        setDescriptor("Wet");
        setCharacter(' ');
        setfGColor(TileColor.transparent());
        setbGColor(TileColor.create(liquid.getbGColor().getRed(), liquid.getbGColor().getGreen(), liquid.getbGColor().getBlue(), 100));
        setModifiers(new HashSet<>(List.of(Modifiers.blink())));
    }

    @Override
    public int behave() {
        this.timer--;
        if (timer <= 0) {
            this.owner.removeStatus(this);
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return Status.isActiveHelper(this);
    }

    @Override
    public boolean filterOut(Status status) {
        if (status instanceof Burning) {
            Entity owner = this.getOwner();
            if (liquid.evaporates())
                owner.getSpace().addTerrain(liquid.getEvaporationGas(1));
            owner.removeStatus(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean filterIn(Status status) {
        if (status instanceof Wet otherWet && otherWet.liquid.getClass() == this.liquid.getClass()) {
            otherWet.timer++;
            return true;
        }
        if (status instanceof Burning burning) {
            Entity owner = burning.getOwner();
            if (liquid.evaporates())
                owner.getSpace().addTerrain(liquid.getEvaporationGas(1));
            owner.removeStatus(burning);
            return true;
        }
        return false;
    }

}
