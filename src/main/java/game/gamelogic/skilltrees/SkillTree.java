package game.gamelogic.skilltrees;

import java.util.HashSet;
import java.util.Set;

import game.gamelogic.HasName;

public abstract class SkillTree implements HasName {

    protected Set<SkillEntry> skillEntries = new HashSet<>();

    public Set<SkillEntry> getSkillEntries() {
        return skillEntries;
    }

    public boolean removeSkillEntry(SkillEntry entry){
        return skillEntries.remove(entry);
    }

    public boolean addSkillEntry(SkillEntry entry){
        return skillEntries.add(entry);
    }

    protected String name;
    protected String description;

    @Override
    public String getName() {
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }
}
