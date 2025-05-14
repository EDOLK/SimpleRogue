package game.gamelogic;

import game.gamelogic.SkillMap.Skill;

public interface HasSkills {

    public SkillMap getSkillMap();

    default int getSkill(Skill skill){
        return getSkillMap().get(skill);
    }

    default void setSkill(Skill skill, int amount){
        getSkillMap().set(skill,amount);
    }

    default int getSkillPoints(){
        return -1;
    }

    default void setSkillPoints(int att){
        
    }

}
