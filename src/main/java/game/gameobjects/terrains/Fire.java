package game.gameobjects.terrains;

import static game.App.lerp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.Examinable;
import game.gamelogic.Flammable;
import game.gamelogic.LightSource;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Burning;

public class Fire extends Terrain implements Behavable, SelfAware, Examinable, LightSource{
    
    protected static final int MAX_FUEL = 20;

    public static boolean isFlammable(Space space){
        for (Item item : space.getItems()) {
            if (item instanceof Flammable){
                return true;
            }
        }
        for (Terrain terrain : space.getTerrains()) {
            if (terrain instanceof Flammable){
                return true;
            }
        }
        return false;
    }

    private Space space;
    private int fuel;
    protected String name = "Fire";
    protected String description = "This is fire.";
    protected String subHeader = "";
    protected String stats = "";
    
    
    public Fire(int fuel){
        this.fuel = fuel;
        setfGColor(TileColor.create(255, 72, 0, 255));
        setbGColor(TileColor.create(250, 205, 0, 255));
        setCharacter('▓');
        setTileName("Fire");
        setModifiers(new HashSet<Modifier>(List.of(Modifiers.blink())));
    }
    
    public int getFuel() {
        return fuel;
    }
    
    public void addFuel(int amount){
        fuel += amount;
        if (fuel > MAX_FUEL){
            fuel = MAX_FUEL;
        }
    }
    
    public void subtractFuel(int amount){
        fuel -= amount;
        if (fuel < 0){
            fuel = 0;
        }
    }

    @Override
    public Space getSpace() {
        return space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public int behave() {

        if (fuel <= 0){
            getSpace().removeTerrain(this);
            setSpace(null);
            return 100;
        }

        Flammable flammable = null;

        for (Terrain terrain : space.getTerrains()) {
            if (terrain instanceof Flammable flammableTerrain){
                flammable = flammableTerrain;
                break;
            }
        }

        if (flammable == null || Math.random() < 0.50){
            for (Item item : space.getItems()) {
                if (item instanceof Flammable flammableItem){
                    flammable = flammableItem;
                    break;
                }
            }
        }
        
        if (flammable != null){
            addFuel(flammable.getFuelValue());
            flammable.onBurn();
        }

        
        if (space.isOccupied()){
            space.getOccupant().addStatus(new Burning());
        }

        List<Terrain> terrainsToAdd = new ArrayList<>();
        List<Terrain> terrainsToRemove = new ArrayList<>();

        for (Terrain terrain : getSpace().getTerrains()) {

            if (terrain instanceof Melts melts && melts.melts()) {
                if (terrain instanceof SpreadableTerrain st) {
                    int a = Math.min(this.fuel, st.getAmount());
                    this.fuel -= a;
                    st.setAmount(st.getAmount() - a);
                    terrainsToAdd.add(melts.getMeltingLiquid(a));
                } else {
                    terrainsToRemove.add(terrain);
                    terrainsToAdd.add(melts.getMeltingLiquid(1));
                    this.fuel--;
                }
            }

        }

        terrainsToAdd.forEach(getSpace()::addTerrain);
        terrainsToRemove.forEach(getSpace()::remove);

        for (Space space : Space.getAdjacentSpaces(getSpace())) {
            if (isFlammable(space)) {
                space.addFire(new Fire(1));
            }
        }

        fuel--;

        return 100;
    }

    public String getName() {
        return name + " (" + fuel + ")";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    public String getStats() {
        return stats;
    }

    public void setStats(String stats) {
        this.stats = stats;
    }

    @Override
    public boolean isActive() {
        return getSpace() != null;
    }

    @Override
    public int getLightSourceIntensity() {
        return (int)lerp(1, 5, MAX_FUEL, 20, fuel);
    }

}
