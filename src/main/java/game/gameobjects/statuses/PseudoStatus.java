package game.gameobjects.statuses;

import java.util.Set;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.modifier.Modifier;

public class PseudoStatus extends Status {
    private Status innerStatus;

    public PseudoStatus(Status innerStatus) {
        super();
        this.innerStatus = innerStatus;
    }
    public int getxOffset() {
        return innerStatus.getxOffset();
    }

    public int getyOffset() {
        return innerStatus.getyOffset();
    }

    public boolean isFullBright() {
        return innerStatus.isFullBright();
    }

    public String getDescriptor() {
        return innerStatus.getDescriptor();
    }

    public TileColor getBgColor() {
        return innerStatus.getBgColor();
    }

    public TileColor getFgColor() {
        return innerStatus.getFgColor();
    }

    public char getCharacter() {
        return innerStatus.getCharacter();
    }

    public Set<Modifier> getModifiers() {
        return innerStatus.getModifiers();
    }

    public String getTileName() {
        return innerStatus.getTileName();
    }

    public Tile getTile() {
        return innerStatus.getTile();
    }

    public Tile getTile(double percent) {
        return innerStatus.getTile(percent);
    }

    public String toString() {
        return innerStatus.toString();
    }

}

