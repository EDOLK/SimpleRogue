package game.gamelogic;

import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.display.Display;
import game.display.KeyMap.Action;
import game.display.PauseMenu;
import game.gamelogic.floorinteraction.ExamineSelector;

public interface OverridesPlayerInput {
    default UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase){
        Action action = Display.getKeyMap().getAction(event.getCode());
        if (action == Action.ESCAPE) {
            Display.setMenu(new PauseMenu());
            return UIEventResponse.processed();
        }
        if (action == Action.EXAMINE_TOGGLE){
            Display.getRootMenu().startSelecting(new ExamineSelector());
            return UIEventResponse.processed();
        }
        return UIEventResponse.pass();
    };
}
