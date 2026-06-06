package game.display.animations;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;

import game.display.FloorMenu;
import game.display.TileReference;
import kotlin.Pair;

public class BasicFrame implements Frame {

    private List<TileReference> drawnTiles = new ArrayList<>();
    private List<Pair<Tile,Position>> tilesToBeDrawn = new ArrayList<>();

    public void add(Tile tile, Position position){
        tilesToBeDrawn.add(new Pair<Tile, Position>(tile, position));
    }

    @Override
    public final void draw(FloorMenu menu) {
        for (Pair<Tile, Position> tp : tilesToBeDrawn) {
            drawnTiles.add(menu.draw(tp.getFirst(), tp.getSecond()));
        }
    }

    @Override
    public final void clear() {
        drawnTiles.forEach(TileReference::removeTile);
        drawnTiles.clear();
    }

    
}
