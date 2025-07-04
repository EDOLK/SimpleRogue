package game.gameobjects.terrains.liquids;

import java.util.HashSet;
import java.util.List;
import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.gasses.Gas;
import game.gameobjects.terrains.gasses.Steam;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.statuses.Burning;
import game.gameobjects.statuses.SeperateIn;
import game.gameobjects.statuses.SeperateOut;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.Ice;

public class Water extends Liquid{

    public Water(int depth){
        super(depth);
        setCharacter(' ');
        setfGColor(TileColor.transparent());
        setbGColor(TileColor.create(75, 75, 235, 255));
        setName("Water");
        setTileName("Water");
        setSpreadFactor(1.0f);
    }

    @Override
    public Gas getEvaporationGas(int amount) {
        return new Steam(amount);
    }

    @Override
    public Liquid createSelf(int depth) {
        return new Water(depth);
    }

    @Override
    public int behave() {
        return super.behave();
    }

    @Override
    public boolean evaporates() {
        return true;
    }

    @Override
    public boolean freezes() {
        return true;
    }

	@Override
	public Terrain getFreezeTerrain(int amount) {
        return new Ice(amount);
	}

    @Override
    public Status getWetStatus(int amount) {
        return new Wet();
    }

    public static class Wet extends Status implements SeperateIn, SeperateOut, Behavable {

        private int timer = 20;

        public Wet() {
            super();
            setDescriptor("Wet");
            setCharacter(' ');
            setfGColor(TileColor.transparent());
            setbGColor(TileColor.create(75, 75, 235, 155));
            setModifiers(new HashSet<>(List.of(Modifiers.blink())));
        }

        @Override
        public void onStackIn(Status sameStatus) {
            switch (sameStatus) {
                case Wet w -> {
                    w.timer++;
                }
                case Burning b -> {
                    int amount = (b.getTurns()/2);
                    b.subtractTurns(amount);
                    b.getOwner().getSpace().addTerrain(new Steam(amount));
                }
                default -> {

                }
            }
        }

        @Override
        public boolean validateSamenessIn(Status status) {
            return status instanceof Burning || status instanceof Wet;
        }

        @Override
        public void onStackOut(Status sameStatus) {
            if (sameStatus instanceof Burning b) {
                int amount = (b.getTurns()/2);
                this.timer -= amount;
                this.getOwner().getSpace().addTerrain(new Steam(amount));
            }
        }

        @Override
        public boolean validateSamenessOut(Status status) {
            return status instanceof Burning;
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
            return this.owner != null && this.owner.isAlive();
        }

    }

    @Override
    public boolean wets() {
        return true;
    }

}
