package game.gamelogic.skilltrees.warrior;

import java.util.Optional;
import java.util.function.Consumer;

import game.gamelogic.Attribute;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.skilltrees.SkillTree;
import game.gameobjects.entities.Entity;

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
                        hasAbilities.getAbilities().add(new Clobber(e));
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
                        hasAbilities.getAbilities().add(new SecondWind(e));
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

        SkillEntry bulwarkOne = new SkillEntry.Builder()
            .addName("Bulwark I")
            .addCostRequirement(1)
            .addAttributeRequirement(Attribute.ENDURANCE, 1)
            .addOnApply((e) -> {
                if (e instanceof HasAbilities hasAbilities) {
                    hasAbilities.getAbilities().add(new Bulwark(e));
                }
                this.addSkillEntry(bulwarkTwo);
            })
            .build();

        this.addSkillEntry(bulwarkOne);

    }

}
