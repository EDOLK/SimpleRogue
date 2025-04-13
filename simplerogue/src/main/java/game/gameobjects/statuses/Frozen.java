package game.gameobjects.statuses;

import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.behavior.Behavable;

public class Frozen extends Status implements Seperate, Behavable{

    private int timer = 10;

    public Frozen(int timer) {
        super();
        this.timer = timer;
        this.setCharacter(' ');
        setfGColor(TileColor.transparent());
        setbGColor(TileColor.create(70, 70, 225, 150));
        setDescriptor("Frozen");
    }

    @Override
    public String getDescriptor() {
        return super.getDescriptor() + "(" + timer + ")";
    }

    @Override
    public void onStack(Status sameStatus) {

    }

    @Override
    public Status validateSameness(List<Status> statuses) {
        for (Status status : statuses) {
            if (status instanceof Frozen frozen) {
                return frozen;
            }
        }
        return null;
    }

    @Override
    public void behave() {
        if (timer <= 0) {
            owner.removeStatus(this);
            return;
        }

        timer--;
    }

    @Override
    public boolean isActive() {
        return owner != null && owner.isAlive();
    }

}

