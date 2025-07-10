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
import game.gamelogic.abilities.HasAbilities;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Status;

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
        return recursiveCheckHelper(object, function, new ArrayList<>(), new HashSet<>());
    }

    private static <T> List<T> recursiveCheckHelper(Object object, Function<Object, Optional<T>> function, List<T> ts, Set<Object> checkedObjects){
        Consumer<Object> helper = (obj) -> recursiveCheckHelper(obj,function,ts,checkedObjects);
        if (object != null && checkedObjects.add(object)) {
            Optional<T> optional = function.apply(object);
            if (optional.isPresent()) {
                ts.add(optional.get());
            }
            if (object instanceof Entity entity) {
                entity.getStatuses().forEach(helper::accept);
            }
            if (object instanceof Armed armed) {
                armed.getWeapons().forEach(helper::accept);
            }
            if (object instanceof Armored armored) {
                armored.getArmor().forEach(helper::accept);
            }
            if (object instanceof HasAbilities hasAbilities) {
                hasAbilities.getAbilities().forEach(helper::accept);
            }
            if (object instanceof Weapon weapon) {
                helper.accept(weapon.getEnchantment());
            }
            if (object instanceof Armor armor) {
                helper.accept(armor.getEnchantment());
            }
        }
        return ts;
    }

}
