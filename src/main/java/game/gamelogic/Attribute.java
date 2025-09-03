package game.gamelogic;

import java.util.Optional;

import game.App;
import game.CheckConditions;
import game.gameobjects.entities.Entity;

public enum Attribute {
    STRENGTH("STR"),
    DEXTERITY("DEX"),
    ENDURANCE("END"),
    INTELLIGENCE("INT"),
    CHARISMA("CHA"),
    WISDOM("WIS"),
    LUCK("LCK");

    public final String shortHand;

    private Attribute(String shortHand){
        this.shortHand = shortHand;
    }

    public static int getAttribute(Attribute attribute, Entity entity){
        int val = 0;
        for (HasAttributes hasAttributes : App.recursiveCheck(entity, getCheckConditions(), (obj) -> {
            if (obj instanceof HasAttributes ha) {
                return Optional.of(ha);
            }
            return Optional.empty();
        })) {
            val += hasAttributes.getAttribute(attribute);
        }
        return val;
    }

    private static CheckConditions getCheckConditions(){
        return CheckConditions.none()
            .withArmors(true)
            .withStatuses(true)
            .withAbility(true)
            .withArmedWeapons(true)
            .withEnchantments(true)
            .withUnarmedWeapon(true);
    }
}
