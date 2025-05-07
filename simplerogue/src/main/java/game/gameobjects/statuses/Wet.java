package game.gameobjects.statuses;

import static game.App.lerp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.behavior.Behavable;
import game.gameobjects.terrains.liquids.Liquid;

public class Wet extends Status implements Behavable, Seperate{
    
    private Liquid liquid;
    private int turns = 0;
    private int evaporationRate;
    private int wetness;

    private final int MAX_WETNESS = Liquid.MAX_DEPTH;

    public Wet(Liquid liquid) {
        this.liquid = liquid;
        evaporationRate = liquid.getEvaporationRate();
        wetness = liquid.getDepth();
        setCharacter(' ');
        setDescriptor("Wet");
        Set<Modifier> modSet = new HashSet<Modifier>();
        modSet.add(Modifiers.blink());
        setModifiers(getModifiers());
        setfGColor(TileColor.transparent());
        setbGColor(liquid.getbGColor());
    }

    @Override
    public TileColor getbGColor() {
        return super.getbGColor().withAlpha((int)lerp(0, 50, MAX_WETNESS, 235, getWetness()));
    }

    public Liquid getLiquid() {
        return liquid;
    }

    public void setLiquid(Liquid liquid) {
        this.liquid = liquid;
    }

    public int getWetness() {
        return wetness;
    }
    
    public void setWetness(int wetness) {
        if (wetness > MAX_WETNESS){
            this.wetness = MAX_WETNESS;
        } else if (wetness < 0){
            this.wetness = 0;
        } else {
            this.wetness = wetness;
        }
    }

    public void addWetness(int amount){
        setWetness(getWetness() + amount);
    }

    public void subtractWetness(int amount){
        setWetness(getWetness() - amount);
    }

    @Override
    public int behave() {
        Burning burning = (Burning)owner.getStatusByClass(Burning.class);
        if (burning != null){
            int t = burning.getTurns();
            int amount = Math.min(t, getWetness());
            burning.subtractTurns(amount);
            subtractWetness(amount);
            owner.getSpace().addGas(liquid.getEvaporationGas(amount));
        }

        if (turns >= evaporationRate){
            turns = 0;
            wetness--;
        } else {
            turns++;
        }

        if (wetness <= 0){
            owner.removeStatus(this);
        }

        return 100;

    }

    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }

    @Override
    public void onStack(Status SameStatus) {
        Wet wet = (Wet)SameStatus;
        wet.setWetness(Math.max(this.wetness, wet.getWetness()));
    }

    @Override
    public Status validateSameness(List<Status> Statuses) {
        for (Status status : Statuses) {
            if (status instanceof Wet wet && wet.getLiquid().getClass().isInstance(liquid)){
                return wet;
            }
        }
        return null;
    }

}
