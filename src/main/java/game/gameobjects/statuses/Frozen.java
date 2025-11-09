package game.gameobjects.statuses;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.Dungeon;
import game.display.Display;
import game.display.KeyMap.Action;
import game.gamelogic.OverridesBehavable;
import game.gamelogic.OverridesPlayerInput;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.terrains.gasses.Steam;

public class Frozen extends Status implements FiltersIn, FiltersOut, Behavable, OverridesBehavable, OverridesPlayerInput {

    private int turns = 5;

    public Frozen() {
        super();
        this.setCharacter(' ');
        setFgColor(TileColor.transparent());
        setBgColor(TileColor.create(100, 100, 255, 100));
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
    public UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase) {
        Action action = Display.getKeyMap().getAction(event.getCode());
        if (action != Action.ESCAPE && action != Action.EXAMINE_TOGGLE) {
            Dungeon.update(this.owner.getTimeToWait());
            Display.update();
        }
        return OverridesPlayerInput.super.handleKeyboardEvent(event, phase);
    }

    @Override
    public boolean filterIn(Status status) {
        switch (status) {
            case Freezing freezing -> {
                freezing.getOwner().removeStatus(freezing);
                return false;
            }
            case Burning burning -> {
                burning.getOwner().getSpace().addTerrain(new Steam(1));
                burning.getOwner().removeStatus(burning);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public boolean filterOut(Status status) {
        switch (status) {
            case Frozen frozen -> {
                return true;
            }
            case Freezing freezing -> {
                this.turns++;
                return true;
            }
            case Burning burning -> {
                this.owner.getSpace().addTerrain(new Steam(1));
                this.owner.removeStatus(this);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public void onStatusAdd() {
        if (this.owner instanceof PlayerEntity) {
            Display.log("You are frozen!", (style) -> {
                return style
                    .withForegroundColor(style.getForegroundColor().withBlue(255));
            });
        }
    }
}
