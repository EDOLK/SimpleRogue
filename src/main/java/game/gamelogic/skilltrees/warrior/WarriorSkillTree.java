package game.gamelogic.skilltrees.warrior;

import java.util.Optional;

import game.gamelogic.Attribute;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.skilltrees.MultiSkillEntryBuilder;
import game.gamelogic.skilltrees.SkillEntryBuilder;
import game.gamelogic.skilltrees.SkillTree;

public class WarriorSkillTree extends SkillTree {

    public WarriorSkillTree() {
        super();

        this.name = "Warrior";

        this.addSkillEntry(
            new SkillEntryBuilder()
                .addName("Clobber")
                .addCostRequirement(1)
                .addAttributeRequirement(Attribute.STRENGTH, 1)
                .addOnApply((e) -> {
                    if (e instanceof HasAbilities hasAbilities)
                        hasAbilities.addAbility(new Clobber(e));
                })
                .build()
        );

        this.addSkillEntry(
            new SkillEntryBuilder()
                .addName("Second Wind")
                .addCostRequirement(1)
                .addAttributeRequirement(Attribute.DEXTERITY, 1)
                .addOnApply((e) -> {
                    if (e instanceof HasAbilities hasAbilities)
                        hasAbilities.addAbility(new SecondWind(e));
                })
                .build()
        );

        addSkillEntry(
            new MultiSkillEntryBuilder()
                .withStrategy((i) -> {
                    String str = "Bulwark ";
                    for (int j = 0; j < i; j++) {
                        str += "I";
                    }
                    return new SkillEntryBuilder()
                        .addName(str)
                        .addAttributeRequirement(Attribute.ENDURANCE, i <= 2 ? 1 : 2)
                        .addOnApply((e) -> {
                            if (e instanceof HasAbilities hasAbilities){
                                Optional<Bulwark> bulwark = hasAbilities.getAbilityByClass(Bulwark.class);
                                if (bulwark.isPresent()) {
                                    bulwark.get().setLevel(i);
                                } else {
                                    hasAbilities.addAbility(new Bulwark(e));
                                }
                            }
                        });
                }).chainBuild(this, 1, 3).build()
        );

        addSkillEntry(
            new MultiSkillEntryBuilder()
                .withStrategy((i) -> {
                    String str = "Reliable Combatant ";
                    for (int j = 0; j < i; j++) {
                        str += "I";
                    }
                    return new SkillEntryBuilder()
                        .addName(str)
                        .addCostRequirement(i)
                        .addAttributeRequirement(Attribute.DEXTERITY, i <= 2 ? 1 : 2)
                        .addOnApply((e) -> {
                            if (e instanceof HasAbilities hasAbilities){
                                Optional<ReliableCombatant> reliableCombatant = hasAbilities.getAbilityByClass(ReliableCombatant.class);
                                if (reliableCombatant.isPresent()) {
                                    reliableCombatant.get().setLevel(i);
                                } else {
                                    hasAbilities.addAbility(new ReliableCombatant(e));
                                }
                            }
                        });
                }).chainBuild(this, 1, 3).build()
        );
    }

}
