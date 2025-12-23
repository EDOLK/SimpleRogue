package game.gamelogic.floorinteraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class SelectorHookMap {

    private static Map<Selector, List<Consumer<SelectionResult>>> hooks = new HashMap<>();

    static void put(Selector selector, Consumer<SelectionResult> consumer){
        hooks.computeIfPresent(selector, (k,v) -> {
            v.add(consumer);
            return v;
        });
        hooks.putIfAbsent(selector, new ArrayList<>(List.of(consumer)));
    }

    static void remove(Selector selector, Consumer<SelectionResult> consumer){
        hooks.computeIfPresent(selector, (k,v) -> {
            v.remove(consumer);
            if (v.isEmpty())
                return null;
            return v;
        });
    }

    static List<Consumer<SelectionResult>> get(Selector selector){
        if (hooks.containsKey(selector)) {
            return hooks.get(selector);
        }
        return Collections.emptyList();
    }
    
}
