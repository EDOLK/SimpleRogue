package game.gamelogic.skilltrees.warrior;

import java.util.Optional;
import game.gamelogic.Attribute;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.skilltrees.SkillTree;

public class WarriorSkillTree extends SkillTree {

    public WarriorSkillTree() {
        super();

        this.name = "Warrior";

        this.addSkillEntry(
            new SkillEntry.Builder()
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
            new SkillEntry.Builder()
                .addName("Second Wind")
                .addCostRequirement(1)
                .addAttributeRequirement(Attribute.DEXTERITY, 1)
                .addOnApply((e) -> {
                    if (e instanceof HasAbilities hasAbilities)
                        hasAbilities.addAbility(new SecondWind(e));
                })
                .build()
        );

        SkillEntry bulwarkThree = new SkillEntry.Builder()
            .addName("Bulwark III")
            .addCostRequirement(3)
            .addAttributeRequirement(Attribute.ENDURANCE, 2)
            .addOnApply((e) -> {
                if (e instanceof HasAbilities hasAbilities){
                    Optional<Bulwark> bulwark = hasAbilities.getAbilities().stream().filter(a->a instanceof Bulwark).map(a->(Bulwark)a).findFirst();
                    if (bulwark.isPresent())
                        bulwark.get().setLevel(3);
                }
            })
            .build();

        SkillEntry bulwarkTwo = new SkillEntry.Builder()
            .addName("Bulwark II")
            .addCostRequirement(2)
            .addAttributeRequirement(Attribute.ENDURANCE, 1)
            .addOnApply((e) -> {
                if (e instanceof HasAbilities hasAbilities){
                    Optional<Bulwark> bulwark = hasAbilities.getAbilities().stream().filter(a->a instanceof Bulwark).map(a->(Bulwark)a).findFirst();
                    if (bulwark.isPresent())
                        bulwark.get().setLevel(2);
                    this.addSkillEntry(bulwarkThree);
                }
            })
            .build();

        this.addSkillEntry(
            new SkillEntry.Builder()
                .addName("Bulwark I")
                .addCostRequirement(1)
                .addAttributeRequirement(Attribute.ENDURANCE, 1)
                .addOnApply((e) -> {
                    if (e instanceof HasAbilities hasAbilities) {
                        hasAbilities.addAbility(new Bulwark(e));
                    }
                    this.addSkillEntry(bulwarkTwo);
                })
                .build()
        );

        SkillEntry reliableCombatant3 = new SkillEntry.Builder()
            .addName("Reliable Combatant III")
            .addCostRequirement(3)
            .addAttributeRequirement(Attribute.DEXTERITY, 2)
            .addOnApply((e) -> {
                if (e instanceof HasAbilities hasAbilities){
                    Optional<ReliableCombatant> reliableCombatant = hasAbilities.getAbilities().stream().filter(a->a instanceof ReliableCombatant).map(a->(ReliableCombatant)a).findFirst();
                    if (reliableCombatant.isPresent())
                        reliableCombatant.get().setLevel(3);
                }
            })
            .build();

        SkillEntry reliableCombatant2 = new SkillEntry.Builder()
            .addName("Reliable Combatant II")
            .addCostRequirement(2)
            .addAttributeRequirement(Attribute.DEXTERITY, 1)
            .addOnApply((e) -> {
                if (e instanceof HasAbilities hasAbilities){
                    Optional<ReliableCombatant> reliableCombatant = hasAbilities.getAbilities().stream().filter(a->a instanceof ReliableCombatant).map(a->(ReliableCombatant)a).findFirst();
                    if (reliableCombatant.isPresent())
                        reliableCombatant.get().setLevel(2);
                    this.addSkillEntry(reliableCombatant3);
                }
            })
            .build();

        this.addSkillEntry(
            new SkillEntry.Builder()
                .addName("Reliable Combatant I")
                .addCostRequirement(1)
                .addAttributeRequirement(Attribute.DEXTERITY, 1)
                .addOnApply((e) -> {
                    if (e instanceof HasAbilities hasAbilities)
                        hasAbilities.addAbility(new ReliableCombatant(e));
                    this.addSkillEntry(reliableCombatant2);
                })
                .build()
        );


    }

}
