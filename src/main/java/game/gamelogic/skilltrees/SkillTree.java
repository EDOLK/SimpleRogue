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

    public static SkillEntryBuilder chain(SkillTree tree, SkillEntryBuilder... entries){
        if (entries.length <= 0) {
            return null;
        }
        SkillEntryBuilder current = entries[entries.length-1];
        for (int i = entries.length-2; i >= 0; i--) {
            SkillEntry currentBuilt = current.build();
            SkillEntryBuilder b = entries[i];
            b.addOnApply((e) -> {
                tree.addSkillEntry(currentBuilt);
            });
            current = b;
        }
        return current;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }
}
