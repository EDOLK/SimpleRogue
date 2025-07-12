package game.gamelogic.skilltrees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import game.gamelogic.Attribute;
import game.gamelogic.HasAttributes;
import game.gamelogic.HasName;
import game.gamelogic.HasSkills;
import game.gamelogic.Levelable;
import game.gamelogic.Skill;
import game.gamelogic.abilities.Ability;
import game.gamelogic.abilities.HasAbilities;
import game.gameobjects.entities.Entity;
import kotlin.Pair;

public abstract class SkillTree implements HasName {

    protected Set<SkillEntry> skillEntries = new HashSet<>();

    public Set<SkillEntry> getSkillEntries() {
        return skillEntries;
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

    public static class SkillEntry implements HasName {

        private Map<Ability, Integer> abilityRequirements;
        private String description = "Placeholder description";
        private Map<Attribute, Integer> attributeRequirements;
        private Map<SkillTree, Integer> skillTreeRequirements;
        private Consumer<Entity> applyFunction = (e) -> {};
        private Map<Skill, Integer> skillRequirements;
        private String name = "Placeholder name";
        private int levelRequirement = -1;
        private int cost = 0;

        private SkillEntry(){}

        public int getCost() {
            return cost;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public String getDescription(){
            return this.description;
        }

        public boolean checkForLevel(Entity entity){
            if (levelRequirement != -1) {
                if (!(entity instanceof Levelable levelable)) {
                    return false;
                }
                if (levelable.getLevel() < levelRequirement) {
                    return false;
                }
            }
            return true;
        }

        public boolean checkForCost(Entity entity){
            if (cost != 0) {
                if (!(entity instanceof UsesSkillTrees ust)) {
                    return false;
                }
                if (ust.getSkillTreePoints() >= cost) {
                    return true;
                }
            }
            return false;
        }

        public Map<Skill, Boolean> checkForSkill(Entity entity){
            Map<Skill, Boolean> checks = new HashMap<>();
            if (skillRequirements != null) {
                if (!(entity instanceof HasSkills hasSkills)) {
                    return checks;
                }
                for (Skill skill : skillRequirements.keySet()) {
                    if (hasSkills.getSkillMap().get(skill) < this.skillRequirements.get(skill)) {
                        checks.put(skill, false);
                    } else {
                        checks.put(skill, true);
                    }
                }
            }
            return checks;
        }

        public Map<Attribute, Boolean> checkForAttribute(Entity entity){
            Map<Attribute, Boolean> checks = new HashMap<>();
            if (attributeRequirements != null) {
                if (!(entity instanceof HasAttributes hasAttributes)) {
                    return checks;
                }
                for (Attribute attribute : attributeRequirements.keySet()) {
                    if (hasAttributes.getAttribute(attribute) < this.attributeRequirements.get(attribute)) {
                        checks.put(attribute, false);
                    } else {
                        checks.put(attribute, true);
                    }
                }
            }
            return checks;
        }

        public Map<Ability,Boolean> checkForAbility(Entity entity){
            Map<Ability, Boolean> checks = new HashMap<>();
            if (abilityRequirements != null) {
                if (!(entity instanceof HasAbilities hasAbilities)) {
                    return checks;
                }
                for (Entry<Ability, Integer> entry : abilityRequirements.entrySet()) {
                    Ability match = null;
                    for (Ability ability : hasAbilities.getAbilities()) {
                        if (isEqual(entry.getKey(), ability)) {
                            match = ability;
                            break;
                        }
                    }
                    if (match == null) {
                        checks.put(entry.getKey(), false);
                        continue;
                    }
                    if (match instanceof Levelable levelableMatch) {
                        if (entry.getValue() != -1 && levelableMatch.getLevel() < entry.getValue()) {
                            checks.put(entry.getKey(), false);
                            continue;
                        }
                    }
                    checks.put(entry.getKey(), true);
                }
            }
            return checks;
        }

        public Map<SkillTree,Boolean> checkForSkillTree(Entity entity){
            Map<SkillTree, Boolean> checks = new HashMap<>();
            if (skillTreeRequirements != null) {
                if (!(entity instanceof UsesSkillTrees usesSkillTrees)) {
                    return checks;
                }
                for (Entry<SkillTree, Integer> entry : skillTreeRequirements.entrySet()) {

                    if (entry.getValue() <= 0) {
                        continue;
                    }

                    Integer level = usesSkillTrees.getSkillLevels().get(entry.getKey());

                    level = level == null ? 0 : level;

                    if (level < entry.getValue()) {
                        checks.put(entry.getKey(), false);
                        continue;
                    }

                    checks.put(entry.getKey(), true);

                }
            }
            return checks;
        }

        public Pair<List<String>,List<String>> checkRequirements(Entity entity){
            List<String> fulfilled = new ArrayList<>();
            List<String> unFulfilled = new ArrayList<>();

            if (levelRequirement != -1) {
                String e = "Level " + levelRequirement;
                if (checkForLevel(entity)) {
                    fulfilled.add(e);
                } else {
                    unFulfilled.add(e);
                }
            }

            if (cost != 0) {
                String e = "Cost: " + cost;
                if (checkForCost(entity)) {
                    fulfilled.add(e);
                } else {
                    unFulfilled.add(e);
                }
            }

            Map<Skill,Boolean> skillChecks = checkForSkill(entity);

            for (Entry<Skill,Boolean> entry : skillChecks.entrySet()) {
                if (entry.getValue()) {
                    fulfilled.add(entry.getKey().name() + ": " + skillRequirements.get(entry.getKey()));
                } else {
                    unFulfilled.add(entry.getKey().name() + ": " + skillRequirements.get(entry.getKey()));
                }
            }

            Map<Attribute,Boolean> attributeChecks = checkForAttribute(entity);

            for (Entry<Attribute,Boolean> entry : attributeChecks.entrySet()) {
                if (entry.getValue()) {
                    fulfilled.add(entry.getKey().name() + ": " + attributeRequirements.get(entry.getKey()));
                } else {
                    unFulfilled.add(entry.getKey().name() + ": " + attributeRequirements.get(entry.getKey()));
                }
            }

            Map<Ability,Boolean> abilityChecks = checkForAbility(entity);

            for (Entry<Ability,Boolean> entry : abilityChecks.entrySet()) {
                String i = entry.getKey().getName();
                if (abilityRequirements.get(entry.getKey()) != -1) {
                    i += ": " + abilityRequirements.get(entry.getKey());
                }
                if (entry.getValue()) {
                    fulfilled.add(i);
                } else {
                    unFulfilled.add(i);
                }
            }

            Map<SkillTree,Boolean> skillTreeChecks = checkForSkillTree(entity);

            for (Entry<SkillTree,Boolean> entry : skillTreeChecks.entrySet()) {
                if (entry.getValue()) {
                    fulfilled.add(entry.getKey().getName() + ": " + skillTreeRequirements.get(entry.getKey()));
                } else {
                    unFulfilled.add(entry.getKey().getName() + ": " + skillTreeRequirements.get(entry.getKey()));
                }
            }

            return new Pair<>(fulfilled, unFulfilled);
        }

        public boolean hasRequirements(Entity entity){

            Pair<List<String>,List<String>> checks = checkRequirements(entity);

            if (checks.getSecond().isEmpty()) {
                return true;
            }

            return false;
        }

        private boolean isEqual(Ability a1, Ability a2){
            if (a1 == a2) {
                return true;
            }
            if (a1 == null ^ a2 == null) {
                return false;
            }
            if (a1.getClass() != a2.getClass()) {
                return false;
            }
            return true;
        }

        public void apply(Entity entity){
            this.applyFunction.accept(entity);
        }

        public static class Builder {

            private SkillEntry entry = new SkillEntry();

            public Builder addName(String name){
                entry.name = name;
                return this;
            }

            public Builder addDescription(String description){
                entry.description = description;
                return this;
            }

            public Builder addAttributeRequirement(Attribute attribute, int value){
                if (entry.attributeRequirements == null) {
                    entry.attributeRequirements = new HashMap<>();
                }
                entry.attributeRequirements.put(attribute, value);
                return this;
            }

            public Builder addSkillRequirement(Skill skill, int value){
                if (entry.skillRequirements == null) {
                    entry.skillRequirements = new HashMap<>();
                }
                entry.skillRequirements.put(skill, value);
                return this;
            }

            public Builder addSkillTreeRequirement(SkillTree skillTree, int value){
                if (entry.skillTreeRequirements == null) {
                    entry.skillTreeRequirements = new HashMap<>();
                }
                entry.skillTreeRequirements.put(skillTree, value);
                return this;
            }

            public Builder addAbilityRequirement(Ability ability, int level){
                if (entry.abilityRequirements == null) {
                    entry.abilityRequirements = new HashMap<>();
                }
                entry.abilityRequirements.put(ability, level);
                return this;
            }

            public Builder addLevelRequirement(int level){
                entry.levelRequirement = level;
                return this;
            }

            public Builder addCostRequirement(int cost){
                entry.cost = cost;
                return this;
            }

            public Builder addOnApply(Consumer<Entity> onApply){
                entry.applyFunction = onApply;
                return this;
            }

            public SkillEntry build(){
                return this.entry;
            }

        }

    }
}
