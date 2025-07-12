package game.gamelogic;

public interface HasSkills {

    public SkillMap getSkillMap();

    default int getSkill(Skill skill){
        return getSkillMap().get(skill);
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
