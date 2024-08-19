package game.display;

import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.builder.screen.ScreenBuilder;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.resource.TilesetResource;
import org.hexworks.zircon.api.screen.Screen;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.KeyboardEventType;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

public abstract class Menu {

    protected Screen screen;
    protected Menu previousMenu;
    
    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public Menu getPreviousMenu() {
        return previousMenu;
    }

    public void setPreviousMenu(Menu previousMenu) {
        this.previousMenu = previousMenu;
    }

    public Menu(){
        this(Display.getCharacterTileSet(), Display.getTheme(), Display.getTileGrid());
    }
    
    public Menu(TilesetResource tileSet, ColorTheme colorTheme, TileGrid tileGrid){
        setScreen(ScreenBuilder.createScreenFor(tileGrid));
        getScreen().setTileset(tileSet);
        getScreen().setTheme(colorTheme);
        getScreen().handleKeyboardEvents(KeyboardEventType.KEY_PRESSED, (event, phase) -> {
            return handleKeyboardEvent(event, phase);
        });
    }


    public abstract UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase);

}
