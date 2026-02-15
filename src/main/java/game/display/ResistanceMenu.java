package game.display;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.Header;

import game.gamelogic.HasName;
import game.gamelogic.resistances.FlatResistance;
import game.gamelogic.resistances.PercentageResistance;
import game.gamelogic.resistances.RangeResistance;
import game.gamelogic.resistances.Resistance;
import game.gameobjects.DamageType;

public class ResistanceMenu extends Menu{

    public ResistanceMenu(List<Resistance> resistances){
        List<Header> headers = new ArrayList<Header>();
        List<HasName> names = new ArrayList<>();
        List<String> resistanceStrings = getStrings(resistances);
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

    private static List<String> getStrings(List<Resistance> resistances){
        List<String> resistanceStrings = new ArrayList<>();
        for (DamageType damageType : DamageType.values()) {
            String finalString = damageType.toString().toUpperCase() + ": ";
            int min = 0;
            int max = 0;
            double percent = 0;
            for (Resistance resistance : resistances) {
                if (resistance.getType() != damageType)
                    continue;
                switch (resistance) {
                    case RangeResistance rangeResistance -> {
                        min += rangeResistance.getMinDamage();
                        max += rangeResistance.getMaxDamage();
                    }
                    case FlatResistance flatResistance -> {
                        min += flatResistance.getFlat();
                    }
                    case PercentageResistance percentageResistance -> {
                        percent += ((1 - percent) * percentageResistance.getPercentage());
                    }
                    default -> {

                    }
                }
            }
            percent *= 100;
            if (min != 0 || max != 0) {
                finalString += min;
                if (max != 0)
                    finalString += " - " + max;
            }
            if (percent != 0) {
                if (min != 0 || max != 0)
                    finalString += ", ";
                finalString += (int)percent + "%";
            }
            if (min != 0 || max != 0 || percent != 0)
                resistanceStrings.add(finalString);
        }
        return resistanceStrings;
    }
}
