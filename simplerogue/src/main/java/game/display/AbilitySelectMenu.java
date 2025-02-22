package game.display;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.HasOffHand;
import game.gamelogic.abilities.Ability;
import game.gamelogic.abilities.HasAbility;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;
import kotlin.Pair;

public class AbilitySelectMenu extends Menu{

    private PlayerEntity player;

    public AbilitySelectMenu(PlayerEntity player) {
        super();
        this.player = player;

        List<Ability> abilities = new ArrayList<>(player.getAbilities());
        for (Object object : getAspects(player)) {
            if (object instanceof HasAbility hasAbility) {
                abilities.add(hasAbility.getAbility());
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
        List<Object> list = new ArrayList<>();
        list.add(entity);
        if (entity instanceof Armed armed) {
            list.addAll(armed.getWeapons());
            for (Weapon weapon : armed.getWeapons()) {
                if (weapon.getEnchantment()!=null) {
                    list.add(weapon.getEnchantment());
                }
            }
        }
        if (entity instanceof Armored armored) {
            list.addAll(armored.getArmor());
            for (Armor armor : armored.getArmor()) {
                if (armor.getEnchantment()!=null) {
                    list.add(armor.getEnchantment());
                }
            }
        }
        if (entity instanceof HasOffHand hasOffHand){
            if (hasOffHand.getOffHandSlot().getEquippedItem() != null){
                list.add(hasOffHand.getOffHandSlot().getEquippedItem());
            }
        }
        list.addAll(entity.getStatuses());
        return list;
    }

    @Override
    public Menu refresh() {
        return new AbilitySelectMenu(player);
    }

}
