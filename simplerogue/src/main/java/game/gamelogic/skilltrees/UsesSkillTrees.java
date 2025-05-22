package game.gamelogic.skilltrees;

import java.util.Map;

public interface UsesSkillTrees {
    public Map<SkillTree, Integer> getSkillLevels();
    public int getSkillTreePoints();
    public void setSkillTreePoints(int points);
}
