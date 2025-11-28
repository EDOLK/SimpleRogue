package game.gamelogic.skilltrees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import game.gamelogic.Attribute;
import game.gamelogic.Skill;
import game.gamelogic.abilities.Ability;
import game.gameobjects.entities.Entity;
import kotlin.Pair;

public class MultiSkillEntryBuilder {
    private Function<Integer, String> namingStrategy = (i) -> "Placeholder name";
    private Function<Integer, String> descriptionStrategy = (i) -> "Placeholder description";
    private Collection<Function<Integer, Pair<Ability, Integer>>> abilityRequirementStrategy = new ArrayList<>();
    private Collection<Function<Integer, Pair<Attribute, Integer>>> attributeRequirementStrategy = new ArrayList<>();
    private Collection<Function<Integer, Pair<SkillTree, Integer>>> skillTreeRequirementStrategy = new ArrayList<>();
    private Collection<Function<Integer, Pair<Skill, Integer>>> skillRequirementStrategy = new ArrayList<>();
    private Collection<Function<Integer, Consumer<Entity>>> applyFunctionStrategy = new ArrayList<>();
    private Function<Integer, Integer> levelRequirementStrategy = (i) -> -1;
    private Function<Integer, Integer> costStrategy = (i) -> 0;

    public MultiSkillEntryBuilder setNamingStrategy(Function<Integer, String> namingStrategy) {
        this.namingStrategy = namingStrategy;
        return this;
    }
    public MultiSkillEntryBuilder setDescriptionStrategy(Function<Integer, String> descriptionStrategy) {
        this.descriptionStrategy = descriptionStrategy;
        return this;
    }
    public MultiSkillEntryBuilder addAbilityRequirementStrategy(Function<Integer, Pair<Ability, Integer>> abilityRequirementStrategy) {
        this.abilityRequirementStrategy.add(abilityRequirementStrategy);
        return this;
    }
    public MultiSkillEntryBuilder addAttributeRequirementStrategy(Function<Integer, Pair<Attribute, Integer>> attributeRequirementStrategy) {
        this.attributeRequirementStrategy.add(attributeRequirementStrategy);
        return this;
    }
    public MultiSkillEntryBuilder addSkillTreeRequirementStrategy(Function<Integer, Pair<SkillTree, Integer>> skillTreeRequirementStrategy) {
        this.skillTreeRequirementStrategy.add(skillTreeRequirementStrategy);
        return this;
    }
    public MultiSkillEntryBuilder addSkillRequirementStrategy(Function<Integer, Pair<Skill, Integer>> skillRequirementStrategy) {
        this.skillRequirementStrategy.add(skillRequirementStrategy);
        return this;
    }
    public MultiSkillEntryBuilder addApplyFunctionStrategy(Function<Integer, Consumer<Entity>> applyFunctionStrategy) {
        this.applyFunctionStrategy.add(applyFunctionStrategy);
        return this;
    }
    public MultiSkillEntryBuilder setLevelRequirementStrategy(Function<Integer, Integer> levelRequirementStrategy) {
        this.levelRequirementStrategy = levelRequirementStrategy;
        return this;
    }
    public MultiSkillEntryBuilder setCostStrategy(Function<Integer, Integer> costStrategy) {
        this.costStrategy = costStrategy;
        return this;
    }

    public SkillEntryBuilder chainBuild(SkillTree tree, int from, int to){
        return SkillTree.chain(tree, preBuild(from, to));
    }

    public SkillEntry[] build(int from, int to){
        SkillEntryBuilder[] builders = preBuild(from, to);
        SkillEntry[] entries = new SkillEntry[builders.length];
        for (int i = 0; i < builders.length; i++) {
            entries[i] = builders[i].build();
        }
        return entries;
    }

    private SkillEntryBuilder[] preBuild(int from, int to){
        int amount = Math.abs(to - from) + 1;
        SkillEntryBuilder[] entries = new SkillEntryBuilder[amount];
        int j = 0;
        for (int i = from; i != (from > to ? to-1 : to+1); i += (from > to) ? -1 : 1) {
            SkillEntryBuilder builder = new SkillEntryBuilder()
                .addName(this.namingStrategy.apply(i))
                .addDescription(this.descriptionStrategy.apply(i))
                .addLevelRequirement(this.levelRequirementStrategy.apply(i))
                .addCostRequirement(this.costStrategy.apply(i));
            for (Function<Integer, Pair<Attribute, Integer>> func : attributeRequirementStrategy) {
                Pair<Attribute, Integer> entry = func.apply(i);
                builder = builder.addAttributeRequirement(entry.getFirst(), entry.getSecond());
            }
            for (Function<Integer, Pair<Ability, Integer>> func : abilityRequirementStrategy) {
                Pair<Ability, Integer> entry = func.apply(i);
                builder = builder.addAbilityRequirement(entry.getFirst(), entry.getSecond());
            }
            for (Function<Integer, Pair<Skill, Integer>> func : skillRequirementStrategy) {
                Pair<Skill, Integer> entry = func.apply(i);
                builder = builder.addSkillRequirement(entry.getFirst(), entry.getSecond());
            }
            for (Function<Integer, Pair<SkillTree, Integer>> func : skillTreeRequirementStrategy) {
                Pair<SkillTree, Integer> entry = func.apply(i);
                builder = builder.addSkillTreeRequirement(entry.getFirst(), entry.getSecond());
            }
            for (Function<Integer, Consumer<Entity>> func : applyFunctionStrategy) {
                builder = builder.addOnApply(func.apply(i));
            }
            entries[j] = builder;
            j++;
        }
        return entries;
    }
}
