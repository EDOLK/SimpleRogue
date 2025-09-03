package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import game.display.Display;
import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.HasInventory;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.abilities.HasAbility;
import game.gamelogic.abilities.HasPassives;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;

public class App 
{
    public static void main(String[] args){
        Dungeon.initialize(50, 50);
        Display.initialize(70, 61);
    }

    public static int randomNumber(int min, int max){
        return (int)Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static double randomNumber(double min, double max){
        return Math.random() * (max - min) + min;
    }

    public static double lerp (double x1, double y1, double x2, double y2, double x){
        return y1 + (x - x1) * ((y2-y1)/(x2-x1));
    }
    
    public static <T> T getRandom(List<T> list){
        return list.isEmpty() ? null : list.get(randomNumber(0,list.size()-1));
    }

    public static <T> T removeRandom(List<T> list){
        return list.isEmpty() ? null : list.remove(randomNumber(0,list.size()-1));
    }

    public static <T> List<T> recursiveCheck(Object object, Function<Object, Optional<T>> function){
        return recursiveCheck(object, CheckConditions.all(), function);
    }

    public static <T> List<T> recursiveCheck(Object object, CheckConditions conditions, Function<Object, Optional<T>> function){
        return recursiveCheckHelper(object, conditions, function, new ArrayList<>(), new HashSet<>());
    }

    private static <T> List<T> recursiveCheckHelper(Object object, CheckConditions conditions, Function<Object, Optional<T>> function, List<T> tList, Set<Object> checkedObjects){

        Consumer<Object> helper = (obj) -> recursiveCheckHelper(obj,conditions,function,tList,checkedObjects);

        if (object != null && checkedObjects.add(object)) {

            Optional<T> optional = function.apply(object);

            if (optional.isPresent())
                tList.add(optional.get());

            if (object instanceof Entity entity) {

                if (conditions.includesStatuses())
                    entity.getStatuses().forEach(helper::accept);

                if (conditions.includesUnarmedWeapon())
                    helper.accept(entity.getUnarmedWeapon());

            }

            if (object instanceof HasInventory hasInventory && conditions.includesInventory())
                hasInventory.getInventory().forEach(helper::accept);

            if (object instanceof Armed armed && conditions.includesArmedWeapons())
                armed.getWeapons().forEach(helper::accept);

            if (object instanceof Armored armored && conditions.includesArmors())
                armored.getArmor().forEach(helper::accept);

            if (object instanceof HasAbilities hasAbilities && conditions.includesAbility())
                hasAbilities.getAbilities().forEach(helper::accept);

            if (object instanceof Weapon weapon && conditions.includesEnchantments())
                helper.accept(weapon.getEnchantment());

            if (object instanceof Armor armor && conditions.includesEnchantments())
                helper.accept(armor.getEnchantment());

            if (object instanceof HasPassives hasPassives && conditions.includesPassive())
                hasPassives.getPassives().forEach(helper::accept);
            
            if (conditions.includesAbility()) {

                if (object instanceof HasAbilities hasAbilities)
                    hasAbilities.getAbilities().forEach(helper::accept);

                if (object instanceof HasAbility hasAbility)
                    helper.accept(hasAbility.getAbility());

            }
        }

        return tList;

    }

}
