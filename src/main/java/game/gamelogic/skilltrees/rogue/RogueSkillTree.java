package game.gamelogic.skilltrees.rogue;

import java.util.Optional;

import game.gamelogic.Attribute;
import game.gamelogic.HasSkills;
import game.gamelogic.Skill;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.abilities.HasPassives;
import game.gamelogic.skilltrees.MultiSkillEntryBuilder;
import game.gamelogic.skilltrees.SkillEntryBuilder;
import game.gamelogic.skilltrees.SkillTree;

public class RogueSkillTree extends SkillTree {
    public RogueSkillTree() {
        super();
        this.name = "Rogue";
        addSkillEntry(
                new MultiSkillEntryBuilder()
                .withStrategy((i) -> {
                    return new SkillEntryBuilder()
                        .addName("Skulking " + i + "01")
                        .addCostRequirement(i <= 2 ? 1 : 2)
                        .addAttributeRequirement(Attribute.DEXTERITY, i <= 2 ? 1 : 2)
                        .addOnApply((e) -> {
                            for (int j = 0; j < i; j++) {
                                if (e instanceof HasSkills hasSkills) {
                                    hasSkills.incrementSkill(Skill.STEALTH);
                                }
                            }
                            if (e instanceof HasAbilities hasAbilities) {
                                Optional<Sneak> sneak = hasAbilities.getAbilityByClass(Sneak.class);
                                if (sneak.isPresent()) {
                                    sneak.get().setLevel(i);
                                } else {
                                    hasAbilities.addAbility(new Sneak(e));
                                }
                            }
                        });
                }).chainBuild(this, 1, 3).build()
        );
        addSkillEntry(
            new MultiSkillEntryBuilder()
                .withStrategy((i) -> {
                    String str = "Natural Nightvision ";
                    for (int j = 0; j < i; j++) {
                        str += "I";
                    }
                    return new SkillEntryBuilder()
                        .addName(str)
                        .addSkillTreeRequirement(this, 1 + ((i-1) * 2))
                        .addOnApply((e) -> {
                            e.setNightVisionRange(e.getNightVisionRange() + 1);
                            if (e instanceof HasSkills hSkills) {
                                hSkills.incrementSkill(Skill.PERCEPTION);
                            }
                        });
                }).chainBuild(this, 1, 3).build()
        );
        addSkillEntry(
            new MultiSkillEntryBuilder()
                .withStrategy((i) -> {
                    String str = "Sneak Attack ";
                    for (int j = 0; j < i; j++) {
                        str += "I";
                    }
                    return new SkillEntryBuilder()
                        .addName(str)
                        .addCostRequirement(i)
                        .addOnApply((e) -> {
                            if (e instanceof HasPassives hasPassives) {
                                Optional<SneakAttack> sneakAttack = hasPassives.getPassiveByClass(SneakAttack.class);
                                if (sneakAttack.isPresent()) {
                                    sneakAttack.get().setLevel(i);
                                } else {
                                    hasPassives.addPassive(new SneakAttack(e));
                                }
                            }
                        });
                }).chainBuild(this, 1, 3).build()
        );
    }
}
