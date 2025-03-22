package game.display;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.data.Tile;

import game.gamelogic.Examinable;
import game.gamelogic.HasResistances;
import game.gamelogic.resistances.FlatResistance;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.RangeResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;

public class ResistanceMenu extends Menu{

    public ResistanceMenu(HasResistances hasResistances){
        List<Header> headers = new ArrayList<Header>();
        List<Examinable> examinables = new ArrayList<Examinable>();
        List<String> resistanceStrings = new ArrayList<>();
        int longestOffset = 0;
        for (DamageType damageType : DamageType.values()) {
            String finalString = damageType.toString().toUpperCase() + ": ";
            int min = 0;
            int max = 0;
            int percent = 0;
            for (Resistance resistance : hasResistances.getResistances()) {
                if (resistance.getType() != damageType){
                    continue;
                }
                if (resistance instanceof RangeResistance rangeResistance){
                    min += rangeResistance.getMinDamage() * rangeResistance.getLevel();
                    max += rangeResistance.getMaxDamage() * rangeResistance.getLevel();
                }
                if (resistance instanceof FlatResistance flatResistance){
                    min += flatResistance.getFlat() * flatResistance.getLevel();
                }
                if (resistance instanceof PercentageResistance percentageResistance){
                    double t = 100.0;
                    double p = 0.0;
                    for (int i = 0; i < percentageResistance.getLevel(); i++) {
                        p += percentageResistance.getPercentage() * t;
                        t -= percentageResistance.getPercentage() * t;
                    }
                    percent += (int)p;
                }
                if (max != 0) {
                    finalString += min + " - " + max;
                } else if (min != 0){
                    finalString += min;
                }
                if (percent != 0){
                    if (min != 0 || max != 0) {
                        finalString += ", ";
                    }
                    finalString += percent + "%";
                }
            }
            if (min != 0 || max != 0 || percent != 0){
                int offset = finalString.indexOf(":");
                if (offset > longestOffset) {
                    longestOffset = offset;
                }
                resistanceStrings.add(finalString);
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
            examinables.add(toExaminable(finalString));
        }
        Container container = Display.createFittedContainer(screen, "Resistances", examinables);
        for (Header header : headers) {
            container.addComponent(header);
        }
        screen.addComponent(container);
    }
    
    private Examinable toExaminable(String name){
        final String n = name;
        return new Examinable() {

            @Override
            public String getName() {
                return n;
            }

            @Override
            public String getDescription() {
                throw new UnsupportedOperationException("Unimplemented method 'getDescription'");
            }

            @Override
            public Tile getTile() {
                throw new UnsupportedOperationException("Unimplemented method 'getTile'");
            }
            
        };
    }

    
}
