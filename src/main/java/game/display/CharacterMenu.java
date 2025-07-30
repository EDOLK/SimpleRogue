package game.display;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.hexworks.zircon.api.ComponentAlignments;
import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.HBoxBuilder;
import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.builder.component.VBoxBuilder;
import org.hexworks.zircon.api.component.ComponentAlignment;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.HBox;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import game.gamelogic.Attribute;
import game.gamelogic.Skill;
import game.gameobjects.entities.Entity;

public class CharacterMenu extends Menu {

    public CharacterMenu(Entity entity) {
        super();

        Tile tile = entity.getTile();

        Container container = VBoxBuilder.newBuilder()
            .withSize((int)(getScreen().getWidth()/1.5), (int)(getScreen().getHeight()/1.5))
            .withAlignment(ComponentAlignments.alignmentWithin(this.getScreen(), ComponentAlignment.CENTER))
            .withDecorations(ComponentDecorations.box())
            .build();

        this.getScreen().draw(tile, Position.topLeftOf(container).plus(Position.create(1, 1)));

        this.getScreen().addComponent(container);

        container.addComponent(
            new HeaderBuilder()
                .withText("  " + entity.getName())
                .build()
        );

        VBox vBox = VBoxBuilder.newBuilder()
            .withSize(container.getWidth()-2, container.getHeight()-3)
            .build();

        container.addComponent(vBox);

        Function<IntStream, OptionalInt> getLongest = IntStream::max;

        OptionalInt attributeLongest = getLongest.apply(
            List.of(
                Attribute.values()
            ).stream().mapToInt(
                atr -> atr.getName().length()
            )
        );

        OptionalInt skillLongest = getLongest.apply(
            List.of(
                Skill.values()
            ).stream().mapToInt(
                skill -> skill.getName().length()
            )
        );

    }
}
