package game.display;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.hexworks.zircon.api.uievent.KeyCode;

public class KeyMap {
    
    private Map<KeyCode,Action> map = new HashMap<>();
    
    public KeyMap(){
        map.put(KeyCode.NUMPAD_8, Action.UP);
        map.put(KeyCode.NUMPAD_5, Action.CENTER);
        map.put(KeyCode.NUMPAD_2, Action.DOWN);
        map.put(KeyCode.NUMPAD_4, Action.LEFT);
        map.put(KeyCode.NUMPAD_6, Action.RIGHT);
        map.put(KeyCode.NUMPAD_7, Action.UP_LEFT);
        map.put(KeyCode.NUMPAD_9, Action.UP_RIGHT);
        map.put(KeyCode.NUMPAD_1, Action.DOWN_LEFT);
        map.put(KeyCode.NUMPAD_3, Action.DOWN_RIGHT);
        map.put(KeyCode.KEY_I, Action.INVENTORY);
        map.put(KeyCode.KEY_Q, Action.CONSUME);
        map.put(KeyCode.KEY_D, Action.DROP_TOGGLE);
        map.put(KeyCode.KEY_G, Action.GET_TOGGLE);
        map.put(KeyCode.KEY_X, Action.EXAMINE_TOGGLE);
        map.put(KeyCode.KEY_M, Action.MEMORY_TOGGLE);
        map.put(KeyCode.SPACE, Action.INTERACT_TOGGLE);
        map.put(KeyCode.ESCAPE, Action.ESCAPE);
        map.put(KeyCode.KEY_T, Action.THROWING);
        map.put(KeyCode.KEY_E, Action.EQUIPMENT);
        map.put(KeyCode.ENTER, Action.SUBMIT);
        map.put(KeyCode.COMMA, Action.SCROLL_LEFT);
        map.put(KeyCode.PERIOD, Action.SCROLL_RIGHT);
    }
    
    public KeyMap(File keyMapFile){
        this();
        try (Scanner fileInputScanner = new Scanner(keyMapFile)) {
            while (fileInputScanner.hasNextLine()) {
                String[] mappings = fileInputScanner.nextLine().split(":");
                if (mappings.length != 2){
                    continue;
                }
                map.put(KeyCode.valueOf(mappings[0].toUpperCase()), Action.valueOf(mappings[1].toUpperCase()));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void writeToFile(File file){
        try (PrintWriter fileWriter = new PrintWriter(file)) {
            for (Entry<KeyCode,Action> entry : map.entrySet()) {
                fileWriter.println(entry.getKey().toString() + ":" + entry.getValue().toString());
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public Action getAction(KeyCode code){
        Action action = map.get(code);
        return action != null ? action : Action.NOTHING;
    }
    
    public Action removeAction(KeyCode code){
        return map.remove(code);
    }

    public enum Action{
        RIGHT,
        LEFT,
        UP,
        DOWN,
        CENTER,
        UP_RIGHT,
        UP_LEFT,
        DOWN_LEFT,
        DOWN_RIGHT,
        INVENTORY,
        CONSUME,
        DROP_TOGGLE,
        GET_TOGGLE,
        INTERACT_TOGGLE,
        ESCAPE,
        EXAMINE_TOGGLE,
        EQUIPMENT,
        THROWING,
        SUBMIT,
        SCROLL_LEFT,
        SCROLL_RIGHT,
        MEMORY_TOGGLE,
        NOTHING
    }

}
