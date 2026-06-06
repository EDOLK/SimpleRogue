package game.display;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.LayerHandle;

public class TileReference {

    private final Position position;
    private final LayerHandle layer;

    public TileReference(Position position, LayerHandle layer) {
        this.position = position;
        this.layer = layer;
    }

    public Position getPosition() {
        return position;
    }

    public LayerHandle getLayer() {
        return layer;
    }

    public void removeTile(){
        layer.draw(Tile.empty(), position);
    }
    
    public void draw(Tile tile){
        layer.draw(tile, position);
    }

    public Tile getTile(){
        return layer.getTileAtOrNull(position);
    }

}
