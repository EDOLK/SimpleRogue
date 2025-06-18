package game.gameobjects.items;

import static game.App.lerp;
import static game.App.randomNumber;

import org.hexworks.zircon.api.color.ColorInterpolator;
import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.gamelogic.Consumable;
import game.gamelogic.Flammable;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.terrains.Fire;
import game.gameobjects.terrains.gasses.Miasma;

public class Corpse extends Item implements Behavable, Consumable, SelfAware, Flammable{

    private int decay = 0;
    private int decayLimit;
    private ColorInterpolator interpolator;
    private final TileColor decayedColor = TileColor.create(230, 0, 255, 255);
    private Space currentSpace;
    private String originalEntityName;

    public Corpse(Entity entity){
        interpolator = entity.getfGColor().darkenByPercent(0.2).desaturate(0.9).interpolateTo(decayedColor);
        originalEntityName = entity.getOriginalName();
        setCharacter(entity.getCharacter());
        setfGColor(entity.getfGColor().darkenByPercent(0.2));
        setbGColor(entity.getbGColor());
        setName("Corpse of " + entity.getName());
        setDescription("The corpse of a " + entity.getName() + ", it's rotting.");
        setTileName("Generic Corpse");
        setWeight(entity.getBaseWeight());
        decayLimit = getWeight() * 20;
    }

    public String getOriginalEntityName() {
        return originalEntityName;
    }
    
    public int getDecayLimit() {
        return decayLimit;
    }

    public int getDecay() {
        return decay;
    }
    
    @Override
    public String getName() {
        String protoString = super.getName();
        int d = Math.floorDiv(decayLimit, 4);

        if (decay >= d*3) {
            protoString = "Rotten " + protoString;
        } else if (decay >= d*2){
            protoString = "Decayed " + protoString;
        } else if (decay >= d){

        } else {
            protoString = "Fresh " + protoString;
        }
        return protoString;
    }


    @Override
    public int behave() {
        
        if (decay >= decayLimit){
            deleteSelf();
            return 100;
        }

        double pos = lerp(0, 0, decayLimit, 1, decay);
        TileColor newColor = interpolator.getColorAtRatio(pos);
        setfGColor(newColor);
        if (randomNumber(1, 100) <= 10 && decay >= decayLimit/3){
            currentSpace.addTerrain(new Miasma(randomNumber(1, 10)));
        }
        decay++;
        return 100;
    }

    private void deleteSelf(){
        getSpace().remove(this);
    }

    @Override
    public boolean consume(Entity consumer) {
        int decayProgress = (int)lerp(0, 0, decayLimit, consumer.getHP(), decay);

        if (consumer instanceof PlayerEntity){
            Display.log("You eat the " + getName() + ", disgusting.");
        } else {
            Display.log("The " + consumer.getName() + " eats the " + getName() + ".", consumer.getSpace());
        }
        consumer.dealDamage(decayProgress, DamageType.POISON);
        Display.update();
        return true;
    }

    @Override
    public void setSpace(Space space) {
        currentSpace = space;
    }

    @Override
    public Space getSpace(){
        return currentSpace;
    }

    @Override
    public int getFuelValue() {
        return getWeight();
    }

    @Override
    public void onBurn() {
        getSpace().addItem(new CookedCorpse(this));
        getSpace().remove(this);
    }

    @Override
    public boolean isActive() {
        return getSpace() != null;
    }
    
}
