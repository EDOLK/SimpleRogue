package game.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.App;
import game.gamelogic.abilities.Ability;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.abilities.HasAbility;
import game.gameobjects.entities.Entity;
import kotlin.Pair;

public class AbilitySelectMenu extends Menu{

    private HasAbilities hasAbilities;

    public AbilitySelectMenu(HasAbilities hasAbilities) {
        super();
        this.hasAbilities = hasAbilities;

        List<Ability> abilities = new ArrayList<>(hasAbilities.getAbilities());
        if (hasAbilities instanceof Entity entity) {
            for (Object object : getAspects(entity)) {
                if (object instanceof HasAbility hasAbility && hasAbility.getAbility() != null) {
                    abilities.add(hasAbility.getAbility());
                }
            }
        }
        Container container = Display.createFittedContainer(this.screen,"Abilities",abilities);
        List<Pair<Button,Ability>> buttons = Display.populateContainer(
            container,
            (Ability a) -> {
                a.activate();
                return UIEventResponse.processed();
            },
            abilities
        );
        for (Pair<Button,Ability> pair : buttons) {
            pair.getFirst().setDisabled(!pair.getSecond().isEnabled());
        }
        this.screen.addComponent(container);
    }

    public List<Object> getAspects(Entity entity){
        return App.recursiveCheck(entity, (obj) -> {
            if (obj instanceof HasAbility hasAbility) {
                return Optional.of(hasAbility);
            }
            return Optional.empty();
        });
    }

    @Override
    public Menu refresh() {
        return new AbilitySelectMenu(hasAbilities);
    }

}
