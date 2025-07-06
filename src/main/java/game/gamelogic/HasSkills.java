package game.gamelogic;

import game.gamelogic.SkillMap.Skill;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Status;

public interface HasSkills {

    public SkillMap getSkillMap();

    default int getSkill(Skill skill){
        int value = getSkillMap().get(skill);
        if (this instanceof Entity entity) {
            for (Status status : entity.getStatuses()) {
                if (status instanceof ModifiesSkills ms) {
                    value = ms.modifySkill(skill, value);
                }
            }
        }
        return value;
    }

    default void setSkill(Skill skill, int amount){
        getSkillMap().set(skill,amount);
    }

    default void incrementSkill(Skill skill){
        getSkillMap().increment(skill);
    }

    default void decrementSkill(Skill skill){
        getSkillMap().decrement(skill);
    }

    default int getSkillPoints(){
        return -1;
    }

    default void setSkillPoints(int att){
        
    }

}
