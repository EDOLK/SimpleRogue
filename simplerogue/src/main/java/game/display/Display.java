package game.display;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.ColorThemes;
import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.GraphicalTilesetResources;
import org.hexworks.zircon.api.SwingApplications;
import org.hexworks.zircon.api.application.AppConfig;
import org.hexworks.zircon.api.builder.component.ButtonBuilder;
import org.hexworks.zircon.api.builder.component.VBoxBuilder;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.component.Component;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.resource.TilesetResource;
import org.hexworks.zircon.api.screen.Screen;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.gamelogic.HasName;
import game.gameobjects.Space;
import kotlin.Pair;

public class Display {

    private static TilesetResource characterTileSet = CP437TilesetResources.rexPaint16x16();
    private static TilesetResource graphicalTileSet = GraphicalTilesetResources.loadTilesetFromJar(16, 16, "/Testing_Tileset_Final.zip");
    private static ColorTheme theme = ColorThemes.linuxMintDark();
    private static TileGrid tileGrid;

    private static Menu currentMenu;
    private static FloorMenu rootMenu;
    private static Mode mode = Mode.GRAPHICAL;
    //private static Mode mode = Mode.ASCII;

    private static KeyMap keyMap = new KeyMap();
    
    public static KeyMap getKeyMap() {
        return keyMap;
    }

    static{
        File file = Paths.get("keymap").toFile();
        if (!file.exists()){
            try {
                file.createNewFile();
                keyMap.writeToFile(file);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        } else {
            keyMap = new KeyMap(file);
        }
    }

    public static void setTileGrid(TileGrid tileGrid) {
        Display.tileGrid = tileGrid;
    }

    public static Mode getMode() {
        return mode;
    }

    public static void setMode(Mode mode) {
        Display.mode = mode;
    }

    public static void setRootMenu(FloorMenu floorMenu) {
        Display.rootMenu = floorMenu;
    }

    public static FloorMenu getRootMenu() {
        return rootMenu;
    }

    public static Menu getCurrentMenu(){
        return currentMenu;
    }

    public static TilesetResource getGraphicalTileSet() {
        return graphicalTileSet;
    }

    public static TilesetResource getCharacterTileSet() {
        return characterTileSet;
    }

    public static ColorTheme getTheme() {
        return theme;
    }

    public static TileGrid getTileGrid() {
        return tileGrid;
    }
    
    public static void initialize(int screenX, int screenY){
        Display.tileGrid = SwingApplications.startTileGrid(
            AppConfig.newBuilder()
                .withSize(screenX,screenY)
                .withDefaultTileset(getCharacterTileSet())
                .withDefaultGraphicalTileset(getGraphicalTileSet())
                .build()
        );
        FloorMenu floorMenu = new FloorMenu();
        setRootMenu(floorMenu);
        setMenu(floorMenu);
        floorMenu.update();
    }
    
    public static void log(String message){
        rootMenu.addToLog(message);
    }

    public static void log(String message, Space space){
        rootMenu.addToLog(message, space);
    }
    
    public static void update(){
        rootMenu.update();
    }
    
    public static void setMenu(Menu menu){
        menu.setPreviousMenu(currentMenu);
        currentMenu = menu;
        menu.getScreen().display();
    }

    public static void replaceMenu(Menu menu){
        Menu prevMenu = currentMenu.getPreviousMenu();
        currentMenu = menu;
        currentMenu.setPreviousMenu(prevMenu);
        currentMenu.getScreen().display();
    }

    public static void forgetMenus(){
        currentMenu.setPreviousMenu(null);
    }

    public static void setAndForgetMenus(Menu menu){
        setMenu(menu);
        forgetMenus();
    }
    
    public static void revertMenu(){
        if (currentMenu != null){
            Menu prevMenu = currentMenu.getPreviousMenu();
            if (prevMenu != null){
                Menu prePrevMenu = prevMenu.getPreviousMenu();
                prevMenu = prevMenu.refresh();
                prevMenu.setPreviousMenu(prePrevMenu);
                currentMenu = prevMenu;
                currentMenu.getScreen().display();
            }
        }
    }
    
    public static void revertMenu(int amount){
        for (int i = 0; i < amount; i++) {
            revertMenu();
        }
    }

    @SafeVarargs
    public static <L extends HasName> void populateMenu(Menu menu, Function<L,UIEventResponse> function, String label,L... l){
        List<L> list = new ArrayList<L>();
        for (L l2 : l) {
            list.add(l2);
        }
        populateMenu(menu, function, label, list);
    }

    public static <L extends HasName> void populateMenu(Menu menu, Function<L,UIEventResponse> function, String label, List<L> list){
        Container container = createFittedContainer(menu.screen,label,list);
        populateContainer(container, function, list);
        menu.screen.addComponent(container);
    }
    
    @SafeVarargs
    public static <L extends HasName> Container createFittedContainer(Screen screen, String label, L... l){
        List<L> list = new ArrayList<L>();
        for (L l2 : l) {
            list.add(l2);
        }
        return createFittedContainer(screen, label, list);
    }

    public static <L extends HasName> Container createFittedContainer(Screen screen, String label, List<L> list){
        int height = screen.getHeight()/3;
        if (list.size() + 2 > height){
            height = list.size()+2;
        }

        int width = screen.getWidth()/3;
        for (L l : list) {
            if (l.getName().length()+4 > width){
                width = l.getName().length()+4;
            }
        }

        if (width >= screen.getWidth()){
            width = screen.getWidth()-2;
        }

        if (height >= screen.getHeight()){
            height = screen.getHeight()-2;
        }

        return VBoxBuilder.newBuilder()
            .withSize(width, height)
            .withPosition(screen.getWidth()/2 - (width/2) , screen.getHeight()/2 - (height/2))
            .withDecorations(ComponentDecorations.box(BoxType.SINGLE, label))
            .build();
    }
    
    @SafeVarargs
    public static <L extends HasName> List<Pair<Button,L>> populateContainer(Container container, Function<L, UIEventResponse> function, L... l){
        List<L> list = new ArrayList<L>();
        for (L l2 : l) {
            list.add(l2);
        }
        return populateContainer(container, function, list);
    }
    
    public static <L extends HasName> List<Pair<Button,L>> populateContainer(Container container,  Function<L,UIEventResponse> function, List<L> list){
        List<Pair<Button,L>> buttons = new ArrayList<>();
        int i = 0;
        for (L l : list) {
            i++;
            if (i > container.getHeight() - 2){
                break;
            }
            String protoString = l.getName();
            int length = Math.min(protoString.length()+2, container.getWidth()-2);
            int width = Math.ceilDiv(protoString.length(), container.getWidth());
            Button button = null;
            if (container instanceof VBox){

                button = ButtonBuilder.newBuilder()
                    .withText(protoString)
                    .withSize(length, width)
                    .build();

            } else {

                Iterator<Component> o = container.getChildren().iterator();
                int count = 0;
                while (o.hasNext()) {
                    count++;
                    o.next();
                }
                if (i+count > container.getHeight() - 2){
                    break;
                }
                button = ButtonBuilder.newBuilder()
                    .withText(protoString)
                    .withSize(length, width)
                    .withPosition(0,i+count)
                    .build();
            }
            if (button != null){
                button.handleComponentEvents(ComponentEventType.ACTIVATED, (event) -> {
                    return function.apply(l);
                });
                container.addComponent(button);
                buttons.add(new Pair<Button,L> (button,l));
            }
        }
        return buttons;
    }

    public static enum Mode{
        ASCII, GRAPHICAL
    }

}
