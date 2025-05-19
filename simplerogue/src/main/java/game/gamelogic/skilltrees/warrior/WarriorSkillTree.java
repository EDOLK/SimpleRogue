package game.gamelogic.skilltrees.warrior;

import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.skilltrees.SkillTree;

public class WarriorSkillTree extends SkillTree {

    public WarriorSkillTree() {
        super();
        this.name = "Warrior";
        this.skillEntries.add(
            new SkillEntry.Builder()
            .addName("Clobber")
            .addOnApply((e) -> {
                if (e instanceof HasAbilities hae) {
                    hae.getAbilities().add(new Clobber(e));
                }
            })
            .build()
        );
    }

}
