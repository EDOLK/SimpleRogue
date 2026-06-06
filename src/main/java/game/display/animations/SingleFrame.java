package game.display.animations;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;

import game.display.FloorMenu;
import game.display.TileReference;

public class SingleFrame implements Frame {

    private final Tile tile;
    private final Position position;
    private TileReference reference;

    public SingleFrame(Tile tile, Position position) {
        this.tile = tile;
        this.position = position;
    }

    @Override
    public void draw(FloorMenu menu) {
        reference = menu.draw(tile, position);
    }

    @Override
    public void clear() {
        reference.removeTile();
    }

    
}
