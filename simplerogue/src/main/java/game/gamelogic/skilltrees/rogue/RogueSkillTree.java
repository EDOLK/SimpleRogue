package game.gamelogic.skilltrees.rogue;

import game.gamelogic.AttributeMap.Attribute;
import game.gamelogic.HasSkills;
import game.gamelogic.SkillMap.Skill;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.skilltrees.SkillTree;

public class RogueSkillTree extends SkillTree {
    public RogueSkillTree() {
        super();
        this.name = "Rogue";
        this.skillEntries.add(new SkillEntry.Builder()
            .addName("Skulking 101")
            .addCostRequirement(1)
            .addAttributeRequirement(Attribute.DEXTERITY, 1)
            .addOnApply((e) -> {
                if (e instanceof HasSkills hasSkills) {
                    hasSkills.incrementSkill(Skill.STEALTH);
                }
                if (e instanceof HasAbilities hasAbilities) {
                    hasAbilities.getAbilities().add(new Sneak(e));
                }
            })
        .build());
        this.skillEntries.add(new SkillEntry.Builder()
            .addName("Natural Nightvision")
            .addCostRequirement(1)
            .addSkillTreeRequirement(this, 1)
            .addOnApply((e) -> {
                e.setNightVisionRange(e.getNightVisionRange()+1);
            })
        .build());
    }
}
