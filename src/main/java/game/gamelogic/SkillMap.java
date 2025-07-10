package game.gamelogic;

import java.util.HashMap;
import java.util.Map;

public class SkillMap {

    private Map<Skill,Integer> aMap = new HashMap<>();

    public SkillMap() {
        for (Skill skill : Skill.values()) {
            aMap.put(skill,0);
        }
    }

    public SkillMap(Map<Skill,Integer> aMap){
        this.aMap = aMap;
    }

    public int get(Skill skill){
        return aMap.get(skill);
    }

    public void increment(Skill skill){
        aMap.compute(skill, (a,v) -> {
            return (v+1);
        });
    }

    public void decrement(Skill skill){
        aMap.compute(skill, (a,v) -> {
            return (v-1);
        });
    }

    public void set(Skill skill, int amount){
        aMap.put(skill, amount);
    }
}
