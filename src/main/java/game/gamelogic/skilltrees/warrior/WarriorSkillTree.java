package game.gamelogic.skilltrees.warrior;

import game.gamelogic.Attribute;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.skilltrees.SkillTree;

public class WarriorSkillTree extends SkillTree {

    public WarriorSkillTree() {
        super();
        this.name = "Warrior";
        this.skillEntries.add(
            new SkillEntry.Builder()
            .addName("Clobber")
            .addCostRequirement(1)
            .addAttributeRequirement(Attribute.STRENGTH, 1)
            .addOnApply((e) -> {
                if (e instanceof HasAbilities hasAbilities){
                    hasAbilities.getAbilities().add(new Clobber(e));
                }
            })
            .build()
        );
    }

}
