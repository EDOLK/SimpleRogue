package game.display;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.Header;

import game.gamelogic.HasName;
import game.gamelogic.HasResistances;

public class ResistanceMenu extends Menu{

    public ResistanceMenu(HasResistances hasResistances){
        List<Header> headers = new ArrayList<Header>();
        List<HasName> names = new ArrayList<>();
        List<String> resistanceStrings = HasResistances.getStrings(hasResistances);
        int longestOffset = 0;
        for (String string : resistanceStrings) {
            int offset = string.indexOf(":");
            if (offset > longestOffset) {
                longestOffset = offset;
            }
        }
        for (String string : resistanceStrings) {
            String finalString = string;
            for (int i = 0; i < (longestOffset - string.indexOf(":")); i++) {
                finalString = " " + finalString;
            }
            headers.add(
                new HeaderBuilder()
                    .withText(finalString)
                    .build()
            );
            final String fs = finalString;
            names.add(new HasName(){

                @Override
                public String getName() {
                    return fs;
                }

            });
        }
        
        Container container = Display.createFittedContainer(screen, "Resistances", names);
        for (Header header : headers) {
            container.addComponent(header);
        }
        screen.addComponent(container);
    }

    
    
}
