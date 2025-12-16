package game.gameobjects.entities.props;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.display.Display;
import game.gamelogic.Attribute;
import game.gamelogic.HasInventory;
import game.gamelogic.HasResistances;
import game.gamelogic.interactions.HasInteraction;
import game.gamelogic.interactions.Interaction;
import game.gamelogic.interactions.OpenInventoryInteraction;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;

public abstract class ContainerProp extends Entity implements HasInteraction, HasInventory, HasResistances{

    private List<Item> inventory = new ArrayList<Item>();

    public ContainerProp(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
    }

    public ContainerProp() {
    }

    @Override
    public List<Item> getInventory() {
        return this.inventory;
    }

    @Override
    public Item getCorpse() {
        return null;
    }

    @Override
    public int getHardWeightLimit() {
        return 999;
    }

    @Override
    public int defaultInteraction(Entity interactor){
        int str = 0;
        str += Attribute.getAttribute(Attribute.STRENGTH, interactor);
        if ((str*5) >= this.getWeight()) {
            int xOffset = this.getX() - interactor.getX();
            int yOffset = this.getY() - interactor.getY();
            Space nextSpace = Dungeon.getCurrentFloor().getSpace(
                this.getX() + xOffset,
                this.getY() + yOffset
            );
            if (nextSpace.isOccupied()) {
                if (interactor instanceof PlayerEntity) {
                    Display.log("Something's in the way.");
                }
            } else {
                Space oldSpace = this.getSpace();
                Space.moveEntity(this, nextSpace);
                Space.moveEntity(interactor, oldSpace);
                return interactor.getTimeToMove()*2;
            }
        }
        return interactor.getTimeToWait();
    }

    @Override
    public String getDeathMessage() {
        return "The " + getName() + " breaks.";
    }

    @Override
    public List<Resistance> getResistances() {
        List<Resistance> rList = new ArrayList<>();
        for (DamageType type : DamageType.values()) {
            if (type != DamageType.SLASHING && type != DamageType.PIERCING && type != DamageType.BLUNT
                    && type != DamageType.FIRE) {
                rList.add(new PercentageResistance(type, 1.0));
            }
        }
        return rList;
    }

    @Override
    public Interaction getInteraction() {
        return new OpenInventoryInteraction(this);
    }

}
