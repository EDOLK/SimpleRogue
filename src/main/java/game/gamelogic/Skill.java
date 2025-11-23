package game.gamelogic;

import java.util.Optional;

import game.App;
import game.CheckConditions;
import game.gameobjects.Space;
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
            .withAbility(true)
            .withArmedWeapons(true)
            .withEnchantments(true)
            .withUnarmedWeapon(true);
    }

    public static Entity stealthCheck(Entity perceiver, Entity stealther){
        int stealth = App.randomNumber(1, 20);
        int perception = 10;
        stealth += Skill.getSkill(Skill.STEALTH, stealther);
        perception += Skill.getSkill(Skill.PERCEPTION, perceiver);
        stealth += (int)((stealther.getSpace().getLight()-0.50f)*-15);
        int distance = Space.getDistance(perceiver.getSpace(), stealther.getSpace());
        if (distance <= 5) {
            perception += Math.abs(distance-6);
        }
        if (perception >= stealth) {
            return perceiver;
        }
        return stealther;
    }

}
