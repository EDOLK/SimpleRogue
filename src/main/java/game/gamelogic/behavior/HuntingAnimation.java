package game.gamelogic.behavior;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;

import game.display.animations.Frame;
import game.display.animations.IteratorAnimation;
import game.display.animations.SingleFrame;
import game.gameobjects.entities.Animal;

public class HuntingAnimation {

    public static IteratorAnimation create(Animal animal, int fadeDist){
        List<Frame> frames = new ArrayList<>();
        int i = 1;
        int y = animal.getSpace().getY()-i;
        while (y > 0 && i <= fadeDist) {
            y = animal.getSpace().getY()-i;
            frames.add(
                new SingleFrame(
                    Tile.newBuilder()
                        .withCharacter('!')
                        .withForegroundColor(TileColor.create(
                            255 - ((255/fadeDist) * (i - 1)),
                            0,
                            0,
                            255
                        ))
                        .withBackgroundColor(TileColor.transparent())
                        .build(),
                    Position.create(animal.getSpace().getX(), y)
                )
            );
            i++;
        }
        return new IteratorAnimation(frames);
    }

}
