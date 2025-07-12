package game.gamelogic;

import java.util.Optional;

import game.App;
import game.CheckConditions;
import game.gameobjects.entities.Entity;

public enum Skill {
    STEALTH,
    ACROBATICS,
    ATHLETICS,
    WIZARDRY,
    PERCEPTION,
    NATURE;

    public static int getSkill(Skill skill, Entity entity){
        int val = 0;
        for (HasSkills hs : App.recursiveCheck(entity, getCheckConditions(), (obj) -> {
            if (obj instanceof HasSkills hs) {
                return Optional.of(hs);
            }
            return Optional.empty();
        })) {
            val += hs.getSkill(skill);
        }
        for (ModifiesSkills ms : App.recursiveCheck(entity, getCheckConditions(), (obj) -> {
            if (obj instanceof ModifiesSkills ms) {
                return Optional.of(ms);
            }
            return Optional.empty();
        })) {
            val = ms.modifySkill(skill, val);
        }
        return val;
    }

    private static CheckConditions getCheckConditions(){
        return CheckConditions.none()
            .withArmors(true)
            .withStatuses(true)
            .withAbilities(true)
            .withArmedWeapons(true)
            .withEnchantments(true)
            .withUnarmedWeapon(true);
    }

}
