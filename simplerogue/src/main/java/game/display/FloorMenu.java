package game.display;

import static game.App.lerp;
import static game.gameobjects.Space.moveEntity;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.builder.component.LabelBuilder;
import org.hexworks.zircon.api.builder.component.LogAreaBuilder;
import org.hexworks.zircon.api.builder.component.PanelBuilder;
import org.hexworks.zircon.api.builder.graphics.LayerBuilder;
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Label;
import org.hexworks.zircon.api.component.LogArea;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.graphics.Layer;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.Dungeon;
import game.Line;
import game.display.Display.Mode;
import game.display.KeyMap.Action;
import game.gamelogic.Aimable;
import game.gamelogic.Experiential;
import game.gamelogic.Interactable;
import game.gamelogic.Levelable;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Door;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.entities.ThrownItem;
import game.gameobjects.entities.Wall;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.Terrain;
import game.gameobjects.terrains.Trap;
import game.gameobjects.terrains.gasses.Gas;
import game.gameobjects.terrains.liquids.Liquid;

public final class FloorMenu extends Menu{

    private Layer spaceLayer;
    private Layer trapLayer;
    private Layer terrainLayer;
    private Layer lowLiquidLayer;
    private Layer itemLayer;
    private Layer midLiquidLayer;
    private Layer entityLayer;
    private Layer statusLayer;
    private Layer highLiquidLayer;
    private Layer gasLayer;
    private Layer cursorLayer;
    private Layer healthBarLayer;
    private Layer darknessLayer;
    private Layer memoryLayer;
    private Floor currentFloor;
    private LogArea logMessageArea;
    private Header hpText;
    private Header mpText;
    private Header lvlText;
    private Header xpText;
    private Header weightText;
    private Header examineName;
    private Header depthText;
    private Cursor cursor;
    private State currentState = State.INGAME;
    private Aimable thowingItem = null;
    private Item droppingItem = null;

    public Item getDroppingItem() {
        return droppingItem;
    }

    public void setDroppingItem(Item droppingItem) {
        this.droppingItem = droppingItem;
    }

    public Aimable getThowingItem() {
        return thowingItem;
    }

    public void setThowingItem(Aimable thowingItem) {
        this.thowingItem = thowingItem;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public FloorMenu(){
        super();

        currentFloor = Dungeon.getCurrentFloor();

        initializeLog();

        initializeStatusPanel();

        initializeFloorLayers();

        examineName = HeaderBuilder.newBuilder()
            .withText("")
            .withPosition(Position.create(0, currentFloor.SIZE_Y))
            .withSize(currentFloor.SIZE_X,1)
            .build();

        screen.addComponent(examineName);
        
        update();
    }

    public void update(){
        PlayerEntity playerEntity = currentFloor.getPlayer();
        spaceLayer.clear();
        terrainLayer.clear();
        trapLayer.clear();
        lowLiquidLayer.clear();
        itemLayer.clear();
        midLiquidLayer.clear();
        entityLayer.clear();
        statusLayer.clear();
        highLiquidLayer.clear();
        gasLayer.clear();
        cursorLayer.clear();
        healthBarLayer.clear();
        if (Display.getMode() == Mode.GRAPHICAL)
            darknessLayer.clear();
        ArrayList<Space> visibleSpaces = new ArrayList<Space>();
        for (int x = 0; x < currentFloor.SIZE_X; x++) {
            for (int y = 0; y < currentFloor.SIZE_Y; y++){
                Space current = currentFloor.getSpace(x, y);
                if (playerEntity.isWithinVision(current) && current.getLight() != 0){
                    visibleSpaces.add(current);
                    memoryLayer.draw(Tile.empty(), Position.create(x,y));
                }
                // visibleSpaces.add(current);
            }
        }
        

        for (Space space : visibleSpaces) {
            addToLayers(space, playerEntity);
        }

        updatePlayerStatus(playerEntity);
        drawCursor(playerEntity);

    }

    private void addToLayers(Space current, PlayerEntity playerEntity){
        int x = current.getX();
        int y = current.getY();
        float darkness = 1.0f - current.getLight();
        // double darkness = 0;

        spaceLayer.draw(current.getTile(darkness), Position.create(x, y));

        if (!current.getItems().isEmpty()){
            itemLayer.draw(current.getItems().get(0).getTile(darkness), Position.create(x, y));
        }

        if (current.isOccupied()){
            drawEntity(current.getOccupant(), x, y, darkness);
        }

        for (Terrain terrain : current.getTerrains()) {
            drawTerrain( terrain, x, y, darkness);
        }

        if (Display.getMode() == Mode.GRAPHICAL){
            int darkValue = (int)(darkness * 10);
            Tile darknessTile = Tile.newBuilder()
                .withName("Darkness " + darkValue)
                .withTileset(Display.getGraphicalTileSet())
                .buildGraphicalTile();
            darknessLayer.draw(darknessTile, Position.create(x, y));
        }
    }

    private void drawCursor(PlayerEntity playerEntity) {
        if (cursor != null){
            Space cursorSpace = cursor.getSelectedSpace();
            cursorLayer.draw(cursor.getTile(), Position.create(cursorSpace.getX(), cursorSpace.getY()));
            if (cursor.getSelectedSpace().getLight() > 0 && playerEntity.isWithinVision(cursorSpace)) {
                cursor.collectExaminables();
                setExamineTooltip();
            }
        }
    }

    private void drawTerrain(Terrain terrain, int x, int y, double darkness) {
        if (terrain instanceof Liquid liquid){
            if (liquid.getDepth() <= 1){
                lowLiquidLayer.draw(liquid.getTile(darkness), Position.create(x, y));
                return;
            }
            if (liquid.getDepth() <= 5){
                midLiquidLayer.draw(liquid.getTile(darkness), Position.create(x, y));
                return;
            }
            if (liquid.getDepth() <= 10){
                highLiquidLayer.draw(liquid.getTile(darkness), Position.create(x, y));
                return;
            }
        } else if (terrain instanceof Gas gas){
            gasLayer.draw(gas.getTile(darkness), Position.create(x, y));
        } else if (terrain instanceof Trap trap){
            trapLayer.draw(trap.getTile(darkness), Position.create(x, y));
        } else {
            terrainLayer.draw(terrain.getTile(darkness), Position.create(x, y));
        }
    }

    private void drawEntity(Entity occupant, int x, int y, double darkness) {
        entityLayer.draw(occupant.getTile(darkness), Position.create(x, y));
        if (!occupant.getStatuses().isEmpty()){
            statusLayer.draw(occupant.getStatuses().get(0).getTile(darkness), Position.create(x, y));
        }
        if (Display.getMode() == Mode.GRAPHICAL && occupant.getHP() < occupant.getMaxHP()){
            int healthBarValue = (int)lerp(0, 7, occupant.getMaxHP(), 1, occupant.getHP());
            Tile healthBar = Tile.newBuilder()
                .withName("Health Bar " + healthBarValue)
                .withTileset(Display.getGraphicalTileSet())
                .buildGraphicalTile();
            healthBarLayer.draw(healthBar, Position.create(x, y));
        }
        if (Display.getMode() == Mode.ASCII && (occupant instanceof Wall || occupant instanceof Door)){
            int blue = occupant.getTile().getForegroundColor().getBlue() * 2;
            memoryLayer.draw(
                occupant.getTile().withForegroundColor(occupant.getTile().getForegroundColor().withBlue(blue).darkenByPercent(.80)),
                Position.create(x,y)
            );
        } else if (Display.getMode() == Mode.GRAPHICAL){
            String name = null;
            switch (occupant) {
                case Wall wall ->{
                    name = "Wall Memory";
                }
                case Door door ->{
                    name = "Closed Door Memory";
                }
                default -> {

                }
            }
            if (name != null) {
                memoryLayer.draw(
                    Tile.newBuilder()
                    .withName(name)
                    .withTileset(Display.getGraphicalTileSet())
                    .buildGraphicalTile(),
                    Position.create(x,y)
                );
            }
        }
    }

    public void setExamineTooltip(){
        if (cursor.getExamined() != null){
            examineName.setText(cursor.getExamined().getName());
        } else {
            examineName.setText("");
        }
    }
    
    public void addToLog(String message){
        logMessageArea.addListItem(message);
    }
    
    public void addToLog(String message, Space space){
        if (currentFloor.getPlayer().isWithinVision(space)){
            addToLog(message);
        }
    }

    public void toggleExamination(){
        if (cursor == null){
            cursor = new Cursor(currentFloor.getPlayer().getSpace());
            cursor.collectExaminables();
        } else {
            cursor = null;
            examineName.setText("");
        }
    }

    private void initializeLog(){
        Panel logPanel = PanelBuilder.newBuilder()
            .withDecorations(
                ComponentDecorations.box(BoxType.SINGLE, "log")
            )
            .withPosition(0, currentFloor.SIZE_Y+1)
            .withSize(55, 10)
            .build();
        
        logMessageArea = LogAreaBuilder.newBuilder()
            .withPosition(0, 0)
            .withSize(logPanel.getWidth()-2, logPanel.getHeight()-2)
            .build();
        
        logPanel.addComponent(logMessageArea);
        screen.addComponent(logPanel);
    }

    private void initializeStatusPanel(){

        Panel statusPanel = PanelBuilder.newBuilder()
            .withDecorations(
                ComponentDecorations.box(BoxType.SINGLE, "status")
            )
            .withPosition(50, 0)
            .withSize(20,20)
            .build();

        Label hpLabel = LabelBuilder.newBuilder()
            .withText("HP:")
            .build();
        
        statusPanel.addComponent(hpLabel);

        hpText = HeaderBuilder.newBuilder()
            .withPosition(Position.create(3, 0))
            .withSize(7, 1)
            .withText("")
            .build();

        statusPanel.addComponent(hpText);

        Label mpLabel = LabelBuilder.newBuilder()
            .withText("MP:")
            .withPosition(0, 1)
            .build();

        statusPanel.addComponent(mpLabel);
        
        mpText = HeaderBuilder.newBuilder()
            .withPosition(Position.create(3, 1))
            .withSize(7,1)
            .withText("")
            .build();

        statusPanel.addComponent(mpText);

        Label levelLabel = LabelBuilder.newBuilder()
            .withText("Lvl:")
            .withPosition(0,2)
            .build();
        
        statusPanel.addComponent(levelLabel);

        lvlText = HeaderBuilder.newBuilder()
            .withPosition(Position.create(4, 2))
            .withSize(3,1)
            .withText("1")
            .build();

        statusPanel.addComponent(lvlText);

        Label xpLabel = LabelBuilder.newBuilder()
            .withText("XP:")
            .withPosition(0,3)
            .build();
        
        statusPanel.addComponent(xpLabel);

        xpText = HeaderBuilder.newBuilder()
            .withPosition(Position.create(3, 3))
            .withSize(6,1)
            .withText("2/2")
            .build();

        statusPanel.addComponent(xpText);
        
        Label depthLabel = LabelBuilder.newBuilder()
            .withText("Depth:")
            .withPosition(0, 4)
            .build();

        statusPanel.addComponent(depthLabel);

        depthText = HeaderBuilder.newBuilder()
            .withPosition(Position.create(6, 4))
            .withSize(3,1)
            .withText("")
            .build();

        statusPanel.addComponent(depthText);
        
        Label weightLabel = LabelBuilder.newBuilder()
            .withText("Weight:")
            .withPosition(0, 5)
            .build();

        statusPanel.addComponent(weightLabel);
        
        weightText = HeaderBuilder.newBuilder()
            .withPosition(7,5)
            .withSize(7,1)
            .build();

        statusPanel.addComponent(weightText);

        screen.addComponent(statusPanel);
    }

    private void initializeFloorLayers(){

        screen.draw(TileGraphicsBuilder.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .withFiller(Tile.newBuilder()
                .withBackgroundColor(TileColor.create(0, 0, 0, 255))
                .withForegroundColor(TileColor.create(0, 0, 0, 255))
                .withCharacter('█')
                .build()
            )
            .build()
        );

        memoryLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(memoryLayer);

        spaceLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(spaceLayer);
        
        trapLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(trapLayer);
        
        terrainLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(terrainLayer);
        
        lowLiquidLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(lowLiquidLayer);

        itemLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(itemLayer);
        
        midLiquidLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(midLiquidLayer);

        entityLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(entityLayer);
        
        statusLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(statusLayer);
        
        healthBarLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(healthBarLayer);
        
        highLiquidLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(highLiquidLayer);
        
        gasLayer = Layer.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(gasLayer);

        if (Display.getMode() == Mode.GRAPHICAL){
            darknessLayer = Layer.newBuilder()
                .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
                .build();
            screen.addLayer(darknessLayer);
        }

        cursorLayer = LayerBuilder.newBuilder()
            .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
            .build();
        screen.addLayer(cursorLayer);

    }
    
    private void updatePlayerStatus(PlayerEntity player){
        hpText.setText(Integer.toString(player.getHP()) + "/" + Integer.toString(player.getMaxHP()));
        mpText.setText(Integer.toString(player.getMP()) + "/" + Integer.toString(player.getMaxMP()));
        if (player instanceof Levelable levelable){
            lvlText.setText(Integer.toString(levelable.getLevel()));
        }
        if (player instanceof Experiential experiential){
            xpText.setText(Integer.toString(experiential.getXP()) + "/" + Integer.toString(experiential.getXPToNextLevel()));
        }
        depthText.setText(Integer.toString(Dungeon.getCurrentDepth()));
        weightText.setText(Integer.toString(player.getInventoryWeight()) + "/" + Integer.toString(player.getMaxWeight()));
    }


    @Override
    public UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase) {
        switch (currentState) {
            case INGAME:
                return handleInGame(event, phase);
            case INTERACTING:
                return handleInteracting(event, phase);
            case EXAMINE:
                return handleExamining(event, phase);
            case GETTING:
                return handleGetting(event, phase);
            case DROPPING:
                return handleDropping(event, phase);
            case AIMING:
                return handleAiming(event, phase);
            case DEAD:
                return handleDead(event, phase);
            case DROPPING_SINGULAR:
                return handleDroppingSingular(event, phase);
            default:
                break;
        }
        return UIEventResponse.pass();
    }
    
    public UIEventResponse handleInGame(KeyboardEvent event, UIEventPhase phase){
        boolean actionSuccessful = false;
        switch (Display.getKeyMap().getAction(event.getCode())) {
            case UP: //up
                if (event.getCtrlDown()){
                    actionSuccessful = forceHit(0, -1);
                    break;
                }
                actionSuccessful = tryMoveToAdjecent(0, -1);
                break;
            case DOWN: //down
                if (event.getCtrlDown()){
                    actionSuccessful = forceHit(0, 1);
                    break;
                }
                actionSuccessful = tryMoveToAdjecent(0, 1);
                break;
            case LEFT: //left
                if (event.getCtrlDown()){
                    actionSuccessful = forceHit(-1, 0);
                    break;
                }
                actionSuccessful = tryMoveToAdjecent(-1, 0);
                break;
            case RIGHT: //right
                if (event.getCtrlDown()){
                    actionSuccessful = forceHit(1, 0);
                    break;
                }
                actionSuccessful = tryMoveToAdjecent(1, 0);
                break;
            case UP_LEFT: //up-left
                if (event.getCtrlDown()){
                    actionSuccessful = forceHit(-1, -1);
                    break;
                }
                actionSuccessful = tryMoveToAdjecent(-1, -1);
                break;
            case UP_RIGHT: //up-right
                if (event.getCtrlDown()){
                    actionSuccessful = forceHit(1, -1);
                    break;
                }
                actionSuccessful = tryMoveToAdjecent(1, -1);
                break;
            case DOWN_LEFT: //down-left
                if (event.getCtrlDown()){
                    actionSuccessful = forceHit(-1, 1);
                    break;
                }
                actionSuccessful = tryMoveToAdjecent(-1, 1);
                break;
            case DOWN_RIGHT: //down-right
                if (event.getCtrlDown()){
                    actionSuccessful = forceHit(1, 1);
                    break;
                }
                actionSuccessful = tryMoveToAdjecent(1, 1);
                break;
            case CENTER: //wait
                addToLog("waiting...");
                actionSuccessful = true;
                break;
            case INTERACT_TOGGLE: //Interact
                currentState = State.INTERACTING;
                break;
            case ESCAPE: //pause
                Display.setMenu(new PauseMenu());
                break;
            case EXAMINE_TOGGLE: //examining
                toggleExamination();
                currentState = State.EXAMINE;
                break;
            case GET_TOGGLE: //getting
                currentState = State.GETTING;
                break;
            case EQUIPMENT: //equiping
                Display.setMenu(EquipmentMenu.createEquipEquipmentMenu(currentFloor.getPlayer()));
                break;
            case DROP_TOGGLE: //dropping
                currentState = State.DROPPING;
                break;
            case CONSUME: //consuming
                Display.setMenu(ItemSelectMenu.createConsumableSelectMenu(currentFloor.getPlayer()));
                break;
            case INVENTORY: //inventory
                Display.setMenu(ItemSelectMenu.createInventoryMenu(currentFloor.getPlayer()));
                break;
            case THROWING: //throwing
                Display.setMenu(ItemSelectMenu.createThrowMenu(currentFloor.getPlayer()));
                break;
            default:
                return UIEventResponse.pass();
        }
        if (actionSuccessful){
            Dungeon.update();
        }
        update();
        return UIEventResponse.processed();
    }

    public boolean tryMoveToAdjecent(int toX, int toY){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return false;
        }
        PlayerEntity playerEntity = currentFloor.getPlayer();
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space potentialSpace = currentFloor.getSpace(x+toX, y+toY);
        if (potentialSpace.isOccupied()){
            Entity occupant = potentialSpace.getOccupant();
            occupant.defaultInteraction(playerEntity);
            if (occupant instanceof Wall){
                return false;
            }
        } else {
            moveEntity(playerEntity, potentialSpace);
        }
        return true;
    }

    public boolean forceHit(int toX, int toY){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return false;
        }
        PlayerEntity playerEntity = currentFloor.getPlayer();
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space potentialSpace = currentFloor.getSpace(x+toX, y+toY);
        if (potentialSpace.isOccupied()){
            Entity entity = potentialSpace.getOccupant();
            Floor.doAttack(playerEntity, entity);
            return true;
        } else {
            addToLog("You swing at the air.");
            return false;
        }
    }

    public UIEventResponse handleInteracting(KeyboardEvent event, UIEventPhase phase){
        switch (Display.getKeyMap().getAction(event.getCode())){
            case UP: //up
                interactAdjacent(0, -1);
                break;
            case DOWN: //down
                interactAdjacent(0, 1);
                break;
            case LEFT: //left
                interactAdjacent(-1, 0);
                break;
            case RIGHT: //right
                interactAdjacent(1, 0);
                break;
            case UP_LEFT: //up-left
                interactAdjacent(-1, -1);
                break;
            case UP_RIGHT: //up-right
                interactAdjacent(1, -1);
                break;
            case DOWN_LEFT: //down-left
                interactAdjacent(-1, 1);
                break;
            case DOWN_RIGHT: //down-right
                interactAdjacent(1, 1);
                break;
            case CENTER: //center
                interactAdjacent(0,0);
                break;
            case ESCAPE:
                break;
            default:
                break;
        }
        currentState = State.INGAME;
        Dungeon.update();
        Display.update();
        return UIEventResponse.processed();
    }

    private void interactAdjacent(int toX, int toY){
        PlayerEntity playerEntity = currentFloor.getPlayer();
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return;
        }
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space space = currentFloor.getSpace(x+toX, y+toY);

        if (space.isOccupied() && space.getOccupant() instanceof Interactable interactibleEntity){
            interactibleEntity.onInteract(playerEntity);
            return;
        }

        for (Item item : space.getItems()) {
            if (item instanceof Interactable interactibleItem){
                interactibleItem.onInteract(playerEntity);
                return;
            }
        }
        
        for (Terrain terrain : space.getTerrains()){
            if (terrain instanceof Interactable interactibleTerrain){
                interactibleTerrain.onInteract(playerEntity);
                return;
            }
        }

    }
    
    public UIEventResponse handleExamining(KeyboardEvent event, UIEventPhase phase){
        switch (Display.getKeyMap().getAction(event.getCode())) {
            case EXAMINE_TOGGLE:
                toggleExamination();
                currentState = State.INGAME;
                Display.update();
                break;
            case ESCAPE:
                toggleExamination();
                currentState = State.INGAME;
                Display.update();
                break;
            case UP: //up
                moveCursor(0, -1);
                break;
            case DOWN: //down
                moveCursor(0, 1);
                break;
            case LEFT: //left
                moveCursor(-1, 0);
                break;
            case RIGHT: //right
                moveCursor(1, 0);
                break;
            case UP_LEFT: //up-left
                moveCursor(-1, -1);
                break;
            case UP_RIGHT: //up-right
                moveCursor(1, -1);
                break;
            case DOWN_LEFT: //down-left
                moveCursor(-1, 1);
                break;
            case DOWN_RIGHT: //down-right
                moveCursor(1, 1);
                break;
            case SUBMIT: //examine current
                if (getCursor().getExamined() != null){
                    Display.setMenu(new ExamineMenu(getCursor().getExamined()));
                }
                break;
            case INTERACT_TOGGLE: //examine current
                if (getCursor().getExamined() != null){
                    Display.setMenu(new ExamineMenu(getCursor().getExamined()));
                }
                break;
            case SCROLL_LEFT:
                if (getCursor().getExamined() != null){
                    getCursor().previousExaminable();
                    setExamineTooltip();
                }
                break;
            case SCROLL_RIGHT:
                if (getCursor().getExamined() != null){
                    getCursor().nextExaminable();
                    setExamineTooltip();
                }
                break;
            default:
                break;
        }
        return UIEventResponse.processed();
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void moveCursor(int toX, int toY){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return;
        }
        Cursor currentCursor = getCursor();
        int cursorX = currentCursor.getSelectedSpace().getX();
        int cursorY = currentCursor.getSelectedSpace().getY();
        Space toMove = currentFloor.getClampedSpace(cursorX+toX, cursorY+toY);
        currentCursor.setSelectedSpace(toMove);
        update();
    }
    
    public UIEventResponse handleGetting(KeyboardEvent event, UIEventPhase phase){
        switch (Display.getKeyMap().getAction(event.getCode())){
            case UP: //up
                getAdjacent(0, -1);
                break;
            case DOWN: //down
                getAdjacent(0, 1);
                break;
            case LEFT: //left
                getAdjacent(-1, 0);
                break;
            case RIGHT: //right
                getAdjacent(1, 0);
                break;
            case UP_LEFT: //up-left
                getAdjacent(-1, -1);
                break;
            case UP_RIGHT: //up-right
                getAdjacent(1, -1);
                break;
            case DOWN_LEFT: //down-left
                getAdjacent(-1, 1);
                break;
            case DOWN_RIGHT: //down-right
                getAdjacent(1, 1);
                break;
            case CENTER: //center
                getAdjacent(0,0);
                break;
            case ESCAPE:
                currentState = State.INGAME;
                break;
            default:
                break;
        }
        return UIEventResponse.processed();
    }

    public void getAdjacent(int toX, int toY){
        PlayerEntity playerEntity = currentFloor.getPlayer();
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return;
        }
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space potentialSpace = currentFloor.getSpace(x+toX, y+toY);
        // Display.setMenu(new GetMenu(potentialSpace,playerEntity));
        Display.setMenu(ItemSelectMenu.createPickupMenu(potentialSpace, playerEntity));
        currentState = State.INGAME;
    }
    
    public UIEventResponse handleDropping(KeyboardEvent event, UIEventPhase phase){
        switch (Display.getKeyMap().getAction(event.getCode())){
            case UP: //up
                dropAdjacent(0, -1);
                break;
            case DOWN: //down
                dropAdjacent(0, 1);
                break;
            case LEFT: //left
                dropAdjacent(-1, 0);
                break;
            case RIGHT: //right
                dropAdjacent(1, 0);
                break;
            case UP_LEFT: //up-left
                dropAdjacent(-1, -1);
                break;
            case UP_RIGHT: //up-right
                dropAdjacent(1, -1);
                break;
            case DOWN_LEFT: //down-left
                dropAdjacent(-1, 1);
                break;
            case DOWN_RIGHT: //down-right
                dropAdjacent(1, 1);
                break;
            case CENTER: //center
                dropAdjacent(0,0);
                break;
            case ESCAPE:
                currentState = State.INGAME;
            default:
                break;
        }
        return UIEventResponse.processed();
    }

    public void dropAdjacent(int toX, int toY){
        PlayerEntity playerEntity = currentFloor.getPlayer();
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return;
        }
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space potentialSpace = currentFloor.getSpace(x+toX, y+toY);
        Display.setMenu(ItemSelectMenu.createDropMenu(potentialSpace, playerEntity));
        currentState = State.INGAME;
    }
    
    public UIEventResponse handleAiming(KeyboardEvent event, UIEventPhase phase){
        switch (Display.getKeyMap().getAction(event.getCode())) {
            case UP: //up
                moveCursor(0, -1);
                break;
            case DOWN: //down
                moveCursor(0, 1);
                break;
            case LEFT: //left
                moveCursor(-1, 0);
                break;
            case RIGHT: //right
                moveCursor(1, 0);
                break;
            case UP_LEFT: //up-left
                moveCursor(-1, -1);
                break;
            case UP_RIGHT: //up-right
                moveCursor(1, -1);
                break;
            case DOWN_LEFT: //down-left
                moveCursor(-1, 1);
                break;
            case DOWN_RIGHT: //down-right
                moveCursor(1, 1);
                break;
            case ESCAPE:
                Display.revertMenu();
                break;
            case INTERACT_TOGGLE:
            case SUBMIT:
                Space aimingSpace = cursor.getSelectedSpace();
                if (currentFloor.getPlayer().getSpace() == aimingSpace){
                    thowingItem.onHit(currentFloor.getPlayer());
                    if (thowingItem.landsOnHit()){
                        thowingItem.onLand(currentFloor.getPlayer().getSpace());
                    }
                } else {
                    List<Space> path = Line.getLineAsListInclusive(currentFloor.getPlayer().getSpace(), aimingSpace);
                    path.remove(0);
                    Space spawnSpace = path.get(0);
                    ThrownItem thrownItem = new ThrownItem(thowingItem, spawnSpace, aimingSpace, 6);
                    if (!spawnSpace.isOccupied()){
                        spawnSpace.setOccupant(thrownItem);
                    } else {
                        thowingItem.onHit(spawnSpace.getOccupant());
                        if (thowingItem.landsOnHit()){
                            thowingItem.onLand(spawnSpace);
                        }
                    }
                }
                if (thowingItem instanceof Item item){
                    currentFloor.getPlayer().removeItemFromInventory(item);
                }
                Display.setAndForgetMenus(Display.getRootMenu());
                Display.update();
                break;
            default:
                break;
        }
        return UIEventResponse.processed();
    }
    
    public UIEventResponse handleDead(KeyboardEvent event, UIEventPhase phase){
        if (Display.getKeyMap().getAction(event.getCode()) == Action.ESCAPE){
            Display.setMenu(new PauseMenu());
        }
        return UIEventResponse.processed();
    }
    
    public UIEventResponse handleDroppingSingular(KeyboardEvent event, UIEventPhase phase){

        FloorMenu floorMenu = Display.getRootMenu();

        switch (Display.getKeyMap().getAction(event.getCode())) {
            case UP: //up
                dropDirect(0, -1);
                floorMenu.update();
                Display.setAndForgetMenus(floorMenu);
                break;
            case DOWN: //down
                dropDirect(0, 1);
                floorMenu.update();
                Display.setAndForgetMenus(floorMenu);
                break;
            case LEFT: //left
                dropDirect(-1, 0);
                floorMenu.update();
                Display.setAndForgetMenus(floorMenu);
                break;
            case RIGHT: //right
                dropDirect(1, 0);
                floorMenu.update();
                Display.setAndForgetMenus(floorMenu);
                break;
            case UP_LEFT: //up-left
                dropDirect(-1, -1);
                Display.setAndForgetMenus(floorMenu);
                break;
            case UP_RIGHT: //up-right
                dropDirect(1, -1);
                floorMenu.update();
                Display.setAndForgetMenus(floorMenu);
                break;
            case DOWN_LEFT: //down-left
                dropDirect(-1, 1);
                floorMenu.update();
                Display.setAndForgetMenus(floorMenu);
                break;
            case DOWN_RIGHT: //down-right
                dropDirect(1, 1);
                floorMenu.update();
                Display.setAndForgetMenus(floorMenu);
                break;
            case ESCAPE:
                Display.revertMenu();
                break;
            default:
                break;
        }
        
        return UIEventResponse.processed();
    }
    
    private void dropDirect(int toX, int toY){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return;
        }
        PlayerEntity playerEntity = currentFloor.getPlayer();
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space space = currentFloor.getSpace(x+toX, y+toY);
        space.addItem(droppingItem);
        currentFloor.getPlayer().removeItemFromInventory(droppingItem);
        update();
    }

    public static enum State{
        INGAME, EXAMINE, GETTING, DROPPING, DEAD, AIMING, INTERACTING, DROPPING_SINGULAR, COLLECTING_LIQUID;
    }

    @Override
    public Menu refresh() {
        this.update();
        return this;
    }
 
}
