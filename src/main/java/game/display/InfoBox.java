package game.display;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.builder.component.VBoxBuilder;
import org.hexworks.zircon.api.component.Component;
import org.hexworks.zircon.api.component.Fragment;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.graphics.BoxType;

public class InfoBox implements Fragment{

    private VBox box;

    public InfoBox(String header, String... strings) {
        OptionalInt longestLength = Arrays.stream(strings).mapToInt(String::length).max();
        box = VBoxBuilder.newBuilder()
            .withSize(
                longestLength.isPresent() ? longestLength.getAsInt()+3 : 3,
                strings.length+2
            )
            .withDecorations(ComponentDecorations.box(BoxType.SINGLE, header))
            .build();
        for (String string : strings) {
            box.addComponent(new HeaderBuilder().withText(string));
        }
    }

    @Override
    public Component getRoot() {
        return box;
    }

    
}
