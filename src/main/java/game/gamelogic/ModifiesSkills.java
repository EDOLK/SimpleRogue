package game.gamelogic;
import game.gamelogic.SkillMap.Skill;

public interface ModifiesSkills {
    public int modifySkill(Skill skill, int value);
}
