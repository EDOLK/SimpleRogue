package game.gamelogic.skilltrees;

import java.util.function.Function;

public class MultiSkillEntryBuilder {

    private Function<Integer, SkillEntryBuilder> strategy;

    public MultiSkillEntryBuilder withStrategy(Function<Integer, SkillEntryBuilder> strategy) {
        this.strategy = strategy;
        return this;
    }

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
    

    public SkillEntryBuilder chainBuild(SkillTree tree, int from, int to){
        return chain(tree, preBuild(from, to));
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
            SkillEntryBuilder builder = strategy.apply(i);
            entries[j] = builder;
            j++;
        }
        return entries;
    }

}
