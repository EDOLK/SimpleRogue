package game.gameobjects.items.scrolls;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.display.ItemSelectMenu;
import game.gamelogic.Flammable;
import game.gamelogic.HasInventory;
import game.gamelogic.Levelable;
import game.gamelogic.Scrollable;
import game.gamelogic.SelfAware;
import game.gamelogic.Upgradable;
import game.gamelogic.Upgrader;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;

public class ScrollOfUpgrade extends Item implements SelfAware, Scrollable, Flammable, Upgrader{

    private Space currentSpace;
    
    public ScrollOfUpgrade(){
        setCharacter('\"');
        setfGColor(TileColor.create(255, 184, 133, 255));
        setbGColor(TileColor.transparent());
        setTileName("Scroll");
        setDescription("A scroll of upgrade. Very flammable.");
        setName("Scroll of upgrade");
        setWeight(1);
    }

    @Override
    public int getFuelValue() {
        return 1;
    }

    @Override
    public void onBurn() {
        Display.log("The " + getName() + " burns up.", currentSpace);
        getSpace().remove(this);
    }

    @Override
    public boolean read(Entity reader) {
        if (reader instanceof HasInventory hasInventory){
            Display.setMenu(ItemSelectMenu.createUpgradeMenu(this, hasInventory));
            return true;
        }
        return false;
    }

    @Override
    public boolean opensMenu() {
        return true;
    }

    @Override
    public Space getSpace() {
        return currentSpace;
    }

    @Override
    public void setSpace(Space space) {
        currentSpace = space;
    }

    @Override
    public boolean doUpgrade(Upgradable upgradable) {
        if (upgradable instanceof Levelable levelable){
            levelable.setLevel(levelable.getLevel() + 1);
            return true;
        }
        return false;
    }
    
}
