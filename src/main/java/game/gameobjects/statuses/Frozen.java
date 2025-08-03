package game.gameobjects.statuses;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.display.KeyMap.Action;
import game.gamelogic.OverridesBehavable;
import game.gamelogic.OverridesPlayerInput;
import game.gamelogic.behavior.Behavable;
import game.Dungeon;
import game.display.Display;

public class Frozen extends Status implements SeperateIn, Behavable, OverridesBehavable, OverridesPlayerInput {

    private int turns = 5;

    public Frozen() {
        super();
        this.setCharacter(' ');
        setfGColor(TileColor.transparent());
        setbGColor(TileColor.create(100, 100, 255, 100));
        setDescriptor("Frozen");
        setTileName("Freezing Air");
    }

    @Override
    public int overrideBehave(Behavable behavable) {
        return 100;
    }

    @Override
    public boolean overrideIsActive(Behavable behavable) {
        return this.owner != null && this.owner.isAlive();
    }

    @Override
    public int behave() {
        turns--;
        if (turns <= 0) {
            this.owner.removeStatus(this);
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return this.owner != null && this.owner.isAlive();
    }

    @Override
    public boolean onStackIn(Status sameStatus) {
        return true;
    }

    @Override
    public boolean validateSamenessIn(Status status) {
        return status instanceof Freezing || status instanceof Frozen;
    }

    @Override
    public UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase) {
        Action action = Display.getKeyMap().getAction(event.getCode());
        if (action != Action.ESCAPE && action != Action.EXAMINE_TOGGLE) {
            Dungeon.update(this.owner.getTimeToWait());
            Display.update();
        }
        return OverridesPlayerInput.super.handleKeyboardEvent(event, phase);
    }

}
