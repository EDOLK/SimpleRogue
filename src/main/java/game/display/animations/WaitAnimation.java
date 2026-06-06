package game.display.animations;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;

import game.gamelogic.SelfAware;

public class WaitAnimation extends Animation{

    private final char[] chars = new char[]{'-','\\','|','/','-'};
    private final SelfAware tracker;
    private int index = 0;

    public WaitAnimation(SelfAware tracker) {
        this.tracker = tracker;
    }

    public static Frame f(final char c, final Position p){
        return new SingleFrame(
            Tile.newBuilder()
                .withCharacter(c)
                .withForegroundColor(TileColor.create(255, 255, 255, 255))
                .withBackgroundColor(TileColor.transparent())
                .build(),
            p
        );
    }

    @Override
    public Frame generateFrame() {
        return f(chars[index++], tracker.getSpace().position());
    }

    @Override
    public boolean isPlaying() {
        return index <= chars.length;
    }

}
