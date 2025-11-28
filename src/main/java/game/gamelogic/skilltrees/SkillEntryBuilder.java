package game.gamelogic.skilltrees;

import java.util.HashMap;
import java.util.function.Consumer;

import game.gamelogic.Attribute;
import game.gamelogic.Skill;
import game.gamelogic.abilities.Ability;
import game.gameobjects.entities.Entity;

public class SkillEntryBuilder {

    private SkillEntry entry = new SkillEntry();

    public SkillEntryBuilder addName(String name){
        entry.name = name;
        return this;
    }

    public SkillEntryBuilder addDescription(String description){
        entry.description = description;
        return this;
    }

    public SkillEntryBuilder addAttributeRequirement(Attribute attribute, int value){
        if (entry.attributeRequirements == null) {
            entry.attributeRequirements = new HashMap<>();
        }
        entry.attributeRequirements.put(attribute, value);
        return this;
    }

    public SkillEntryBuilder addSkillRequirement(Skill skill, int value){
        if (entry.skillRequirements == null) {
            entry.skillRequirements = new HashMap<>();
        }
        entry.skillRequirements.put(skill, value);
        return this;
    }

    public SkillEntryBuilder addSkillTreeRequirement(SkillTree skillTree, int value){
        if (entry.skillTreeRequirements == null) {
            entry.skillTreeRequirements = new HashMap<>();
        }
        entry.skillTreeRequirements.put(skillTree, value);
        return this;
    }

    public SkillEntryBuilder addAbilityRequirement(Ability ability, int level){
        if (entry.abilityRequirements == null) {
            entry.abilityRequirements = new HashMap<>();
        }
        entry.abilityRequirements.put(ability, level);
        return this;
    }

    public SkillEntryBuilder addLevelRequirement(int level){
        entry.levelRequirement = level;
        return this;
    }

    public SkillEntryBuilder addCostRequirement(int cost){
        entry.cost = cost;
        return this;
    }

    public SkillEntryBuilder addOnApply(Consumer<Entity> onApply){
        entry.applyFunction = entry.applyFunction.andThen(onApply);
        return this;
    }

    public SkillEntry build(){
        return this.entry;
    }

}
