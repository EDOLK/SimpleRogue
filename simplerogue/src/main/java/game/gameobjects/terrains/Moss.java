package game.gameobjects.terrains;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Examinable;
import game.gamelogic.Flammable;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.terrains.liquids.Water;

public class Moss extends Terrain implements Flammable, Examinable, SelfAware, Behavable{

    private Space space;

    public Moss() {
        setCharacter('░');
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.create(40,120,40,255));
    }

    @Override
    public String getName() {
        return "Moss";
    }

    @Override
    public String getDescription() {
        return "Moss growing through the cracks of the dungeon floor.";
    }

    @Override
    public int getFuelValue() {
        return 1;
    }

    @Override
    public void onBurn() {
        getSpace().addTerrain(new Ashes());
        getSpace().remove(this);
    }

    @Override
    public Space getSpace() {
        return this.space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public int behave() {
        trySpread(getSpace(), true, true);
        // Space.getAdjacentSpaces(getSpace()).forEach((s) -> Moss.trySpread(s,true,true));
        return 1000;
    }

    public static boolean trySpread(Space space, boolean waterRequired, boolean createSpeadingMoss) {
        Moss moss = null;
        Grass grass = null;
        Water water = null;
        for (Terrain t : space.getTerrains()) {
            if (t instanceof Moss m) {
                moss = m;
            }
            if (t instanceof Grass g) {
                grass = g;
            }
            // TODO: modify setAmount to make amount check unnecessary
            if (t instanceof Water w && w.getAmount() > 0) {
                water = w;
            }
        }
        if (!waterRequired || water != null) {
            if (waterRequired) {
                water.setAmount(water.getAmount()-1);
            }
            if (grass != null) {
                if (createSpeadingMoss) {
                    space.addTerrain(new SpreadingMoss(1));
                }
            } else if (moss != null){
                space.remove(moss);
                space.addTerrain(new Grass());
            } else {
                space.addTerrain(new Moss());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isActive() {
        return getSpace() != null;
    }

    
}
