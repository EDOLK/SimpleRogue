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
import org.hexworks.zircon.api.builder.component.ProgressBarBuilder;
import org.hexworks.zircon.api.builder.graphics.LayerBuilder;
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Label;
import org.hexworks.zircon.api.component.LogArea;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.component.ProgressBar;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.graphics.Layer;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.App;
import game.Dungeon;
import game.Line;
import game.display.Display.Mode;
import game.display.KeyMap.Action;
import game.gamelogic.Aimable;
import game.gamelogic.Experiential;
import game.gamelogic.Interactable;
import game.gamelogic.Levelable;
import game.gamelogic.floorinteraction.Selector;
import game.gamelogic.floorinteraction.SimpleSelector;
import game.gameobjects.DisplayableTile;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Door;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.entities.ThrownItem;
import game.gameobjects.entities.Wall;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.OpenDoor;
import game.gameobjects.terrains.Staircase;
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
    private Selector selector = null;
    private Panel statusPanel;
    private Panel enemyPanel;
    private Header nameHeader;
    private ProgressBar enemyBar;
    private Header enemyHpHeader;
    private int timer = 10;
    private Header timeText;

    public FloorMenu(){
        super();

        currentFloor = Dungeon.getCurrentFloor();

        initializeLog();

        initializeStatusPanel();

        initializeEnemyPanel();

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
        if (!enemyPanel.isHidden()) {
            if (timer <= 0) {
                hideEnemyPanel();
                timer = 10;
            } else {
                timer --;
            }
        }
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
        if (Display.getMode() == Mode.GRAPHICAL){
            healthBarLayer.clear();
            darknessLayer.clear();
        }
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

    public void toggleMemory(){
        memoryLayer.setHidden(!memoryLayer.isHidden());
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
        if (terrain instanceof OpenDoor || terrain instanceof Staircase) {
            drawInMemory(terrain, Position.create(x,y));
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
        if (occupant instanceof Wall || occupant instanceof Door) {
            drawInMemory(occupant, Position.create(x,y));
        }
    }

    public void drawInMemory(DisplayableTile tile, Position position) {
        if (Display.getMode() == Mode.ASCII){
            int blue = tile.getTile().getForegroundColor().getBlue() * 2;
            memoryLayer.draw(
                tile.getTile().withForegroundColor(tile.getTile().getForegroundColor().withBlue(blue).darkenByPercent(.90)),
                position
            );
        } else if (Display.getMode() == Mode.GRAPHICAL){
            if (tile.getTileName() != null) {
                memoryLayer.draw(
                    Tile.newBuilder()
                    .withName(tile.getTileName() + " Memory")
                    .withTileset(Display.getGraphicalTileSet())
                    .buildGraphicalTile(),
                    position
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

    public void addHeaderToLog(String message){
        addHeaderToLog(message, false);
    }
    
    public void addHeaderToLog(String message, Space space){
        addHeaderToLog(message, false, space);
    }

    public void addHeaderToLog(String message, Boolean b, Space space){
        if (currentFloor.getPlayer().isWithinVision(space)){
            addHeaderToLog(message, b);
        }
    }

    public void addHeaderToLog(String message, boolean b){
        logMessageArea.addHeader(message, b);
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

        statusPanel = PanelBuilder.newBuilder()
            .withDecorations(
                ComponentDecorations.box(BoxType.SINGLE, "Status")
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

        timeText = HeaderBuilder.newBuilder()
            .withPosition(0,6)
            .withSize(7,1)
            .build();

        statusPanel.addComponent(timeText);

        screen.addComponent(statusPanel);

    }

    private void initializeEnemyPanel() {

        this.enemyPanel = PanelBuilder.newBuilder()
            .withDecorations(
                ComponentDecorations.box(BoxType.SINGLE, "Enemy")
            )
            .withPosition(Position.bottomLeftOf(this.statusPanel))
            .withSize(20,20)
            .build();

        this.nameHeader = HeaderBuilder.newBuilder()
            .withSize(this.enemyPanel.getWidth()-2,1)
            .withPosition(0,3)
            .build();
        this.enemyPanel.addComponent(this.nameHeader);

        this.enemyBar = ProgressBarBuilder.newBuilder()
            .withPosition(3,1)
            .withSize(this.enemyPanel.getWidth()-5,1)
            .withNumberOfSteps(this.enemyPanel.getWidth()-5)
            .build();
        this.enemyPanel.addComponent(this.enemyBar);

        this.enemyHpHeader = HeaderBuilder.newBuilder()
            .withSize(this.enemyBar.getWidth(),1)
            .withPosition(3,0)
            .build();
        this.enemyPanel.addComponent(this.enemyHpHeader);

        this.screen.addComponent(this.enemyPanel);
        this.enemyPanel.setHidden(true);
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
        if (Display.getMode() == Mode.GRAPHICAL) {
            memoryLayer.setHidden(true);
        }

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

        if (Display.getMode() == Mode.GRAPHICAL) {
            healthBarLayer = Layer.newBuilder()
                .withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y)
                .build();
            screen.addLayer(healthBarLayer);
        }
        
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
        weightText.setText(Integer.toString(player.getInventoryWeight()) + "/" + Integer.toString(player.getHardWeightLimit()));
        timeText.setText(Integer.toString(currentFloor.getLastTime()));
    }


    @Override
    public UIEventResponse handleKeyboardEvent(KeyboardEvent event, UIEventPhase phase) {
        UIEventResponse response = UIEventResponse.pass();
        switch (currentState) {
            case INGAME:
                response = handleInGame(event, phase);
                break;
            case DEAD:
                response = handleDead(event, phase);
                break;
            case SELECTING:
                response = handleSelecting(event, phase);
                break;
            default:
                break;
        }
        // gross but necessary, find better way to do this later.
        if (!currentFloor.getPlayer().isAlive())
            currentState = State.DEAD;

        return response;
    }
    

    public UIEventResponse handleInGame(KeyboardEvent event, UIEventPhase phase){
        int time = -1;
        switch (Display.getKeyMap().getAction(event.getCode())) {
            case UP: //up
                if (event.getCtrlDown()){
                    time = forceHit(0, -1);
                    break;
                }
                time = tryMoveToAdjecent(0, -1);
                break;
            case DOWN: //down
                if (event.getCtrlDown()){
                    time = forceHit(0, 1);
                    break;
                }
                time = tryMoveToAdjecent(0, 1);
                break;
            case LEFT: //left
                if (event.getCtrlDown()){
                    time = forceHit(-1, 0);
                    break;
                }
                time = tryMoveToAdjecent(-1, 0);
                break;
            case RIGHT: //right
                if (event.getCtrlDown()){
                    time = forceHit(1, 0);
                    break;
                }
                time = tryMoveToAdjecent(1, 0);
                break;
            case UP_LEFT: //up-left
                if (event.getCtrlDown()){
                    time = forceHit(-1, -1);
                    break;
                }
                time = tryMoveToAdjecent(-1, -1);
                break;
            case UP_RIGHT: //up-right
                if (event.getCtrlDown()){
                    time = forceHit(1, -1);
                    break;
                }
                time = tryMoveToAdjecent(1, -1);
                break;
            case DOWN_LEFT: //down-left
                if (event.getCtrlDown()){
                    time = forceHit(-1, 1);
                    break;
                }
                time = tryMoveToAdjecent(-1, 1);
                break;
            case DOWN_RIGHT: //down-right
                if (event.getCtrlDown()){
                    time = forceHit(1, 1);
                    break;
                }
                time = tryMoveToAdjecent(1, 1);
                break;
            case CENTER: //wait
                addToLog("waiting...");
                time = Dungeon.getCurrentFloor().getPlayer().getTimeToWait();
                break;
            case INTERACT_TOGGLE: //Interact
                startSelecting(new InteractSelector());
                break;
            case ESCAPE: //pause
                Display.setMenu(new PauseMenu());
                break;
            case EXAMINE_TOGGLE: //examining
                startSelecting(new ExamineSelector());
                break;
            case GET_TOGGLE: //getting
                startSelecting(new GetSelector());
                break;
            case EQUIPMENT: //equiping
                Display.setMenu(EquipmentMenu.createEquipEquipmentMenu(currentFloor.getPlayer()));
                break;
            case DROP_TOGGLE: //dropping
                startSelecting(new DropSelector());
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
            case MEMORY_TOGGLE: //toggle memory
                toggleMemory();
                break;
            case ABILITIES:
                Display.setMenu(new AbilitySelectMenu(currentFloor.getPlayer()));
                break;
            case ATTRIBUTES:
                Display.setMenu(new AttributeMenu(currentFloor.getPlayer()));
                break;
            default:
                return UIEventResponse.pass();
        }
        if (time != -1) {
            Dungeon.update(time);
        }
        update();
        return UIEventResponse.processed();
    }

    public int tryMoveToAdjecent(int toX, int toY){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return -1;
        }
        PlayerEntity playerEntity = currentFloor.getPlayer();
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space potentialSpace = currentFloor.getSpace(x+toX, y+toY);
        if (potentialSpace.isOccupied()){
            Entity occupant = potentialSpace.getOccupant();
            return occupant.defaultInteraction(playerEntity);
        } else {
            moveEntity(playerEntity, potentialSpace);
            return playerEntity.getTimeToMove();
        }
    }

    public int forceHit(int toX, int toY){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return -1;
        }
        PlayerEntity playerEntity = currentFloor.getPlayer();
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space potentialSpace = currentFloor.getSpace(x+toX, y+toY);
        if (potentialSpace.isOccupied()){
            Entity entity = potentialSpace.getOccupant();
            Floor.doAttack(playerEntity, entity);
        } else {
            addToLog("You swing at the air.");
        }
        return playerEntity.getTimeToAttack();
    }

    public void writeEnemyInfo(Entity entity){
        timer = 10;
        if (entity.isAlive()) {
            writeEnemyPanel(entity);
        } else {
            hideEnemyPanel();
        }
    }

    private void writeEnemyPanel(Entity entity){
        this.enemyPanel.setHidden(false);
        this.nameHeader.setText(entity.getName());
        this.screen.draw(entity.getTile(),Position.topLeftOf(this.enemyPanel).plus(Position.create(2,2)));
        this.enemyBar.setProgress(App.lerp(0,0,entity.getMaxHP(),100,entity.getHP()));
        this.enemyHpHeader.setText(entity.getHP() + "/" + entity.getMaxHP());
    }

    private void hideEnemyPanel(){
        this.screen.draw(Tile.empty(),Position.topLeftOf(this.enemyPanel).plus(Position.create(2,2)));
        this.enemyPanel.setHidden(true);
    }

    public void startSelecting(Selector selector){
        this.selector = selector;
        this.currentState = State.SELECTING;
        toggleExamination();
        Display.setAndForgetMenus(this);
        update();
    }

    public void startSelecting(SimpleSelector selector){
        this.selector = selector;
        this.currentState = State.SELECTING;
        Display.setAndForgetMenus(this);
        update();
    }

    private UIEventResponse handleSelecting(KeyboardEvent event, UIEventPhase phase) {
        if (selector instanceof SimpleSelector simpleSelector) {
            return handleSelectingSimple(event, phase, simpleSelector);
        }
        switch (Display.getKeyMap().getAction(event.getCode())) {
            case ESCAPE:
                toggleExamination();
                currentState = State.INGAME;
                Display.update();
                break;
            case UP: //up
                moveCursorWithSelector(0, -1, selector);
                break;
            case DOWN: //down
                moveCursorWithSelector(0, 1, selector);
                break;
            case LEFT: //left
                moveCursorWithSelector(-1, 0, selector);
                break;
            case RIGHT: //right
                moveCursorWithSelector(1, 0, selector);
                break;
            case UP_LEFT: //up-left
                moveCursorWithSelector(-1, -1, selector);
                break;
            case UP_RIGHT: //up-right
                moveCursorWithSelector(1, -1, selector);
                break;
            case DOWN_LEFT: //down-left
                moveCursorWithSelector(-1, 1, selector);
                break;
            case DOWN_RIGHT: //down-right
                moveCursorWithSelector(1, 1, selector);
                break;
            case INTERACT_TOGGLE: //select current
            case SUBMIT: //select current
                if (selector.select(getCursor())) {
                    toggleExamination();
                    currentState = State.INGAME;
                    Display.update();
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

    private UIEventResponse handleSelectingSimple(KeyboardEvent event, UIEventPhase phase, SimpleSelector simpleSelector) {
        switch (Display.getKeyMap().getAction(event.getCode())) {
            case ESCAPE:
                currentState = State.INGAME;
                break;
            case CENTER:
                if (attemptSelect(0, 0, simpleSelector)) {
                    currentState = State.INGAME;
                    Display.update();
                }
                break;
            case UP: //up
                if (attemptSelect(0, -1, simpleSelector)) {
                    currentState = State.INGAME;
                    Display.update();
                }
                break;
            case DOWN: //down
                if (attemptSelect(0, 1, simpleSelector)) {
                    currentState = State.INGAME;
                    Display.update();
                }
                break;
            case LEFT: //left
                if (attemptSelect(-1, 0, simpleSelector)) {
                    currentState = State.INGAME;
                    Display.update();
                }
                break;
            case RIGHT: //right
                if (attemptSelect(1, 0, simpleSelector)) {
                    currentState = State.INGAME;
                    Display.update();
                }
                break;
            case UP_LEFT: //up-left
                if (attemptSelect(-1, -1, simpleSelector)) {
                    currentState = State.INGAME;
                    Display.update();
                }
                break;
            case UP_RIGHT: //up-right
                if (attemptSelect(1, -1, simpleSelector)) {
                    currentState = State.INGAME;
                    Display.update();
                }
                break;
            case DOWN_LEFT: //down-left
                if (attemptSelect(-1, 1, simpleSelector)) {
                    currentState = State.INGAME;
                    Display.update();
                }
                break;
            case DOWN_RIGHT: //down-right
                if (attemptSelect(1, 1, simpleSelector)) {
                    currentState = State.INGAME;
                    Display.update();
                }
                break;
            default:
                break;
        }
        return UIEventResponse.processed();
    }

    private boolean attemptSelect(int toX, int toY, SimpleSelector simpleSelector){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return false;
        }
        PlayerEntity playerEntity = currentFloor.getPlayer();
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space potentialSpace = currentFloor.getSpace(x+toX, y+toY);
        return simpleSelector.simpleSelect(potentialSpace);
    }


    public Cursor getCursor() {
        return cursor;
    }

    public void moveCursorWithSelector(int toX, int toY, Selector selector){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return;
        }
        Cursor currentCursor = getCursor();
        int cursorX = currentCursor.getSelectedSpace().getX();
        int cursorY = currentCursor.getSelectedSpace().getY();
        Space to = currentFloor.getClampedSpace(cursorX+toX, cursorY+toY);
        if (selector.canMove(currentCursor, to)) {
            currentCursor.setSelectedSpace(to);
            update();
        }
    }
    
    public UIEventResponse handleDead(KeyboardEvent event, UIEventPhase phase){
        if (Display.getKeyMap().getAction(event.getCode()) == Action.ESCAPE){
            Display.setMenu(new PauseMenu());
        }
        return UIEventResponse.processed();
    }

    public static enum State{
        INGAME, DEAD, SELECTING;
    }

    @Override
    public Menu refresh() {
        this.update();
        return this;
    }

    private class InteractSelector implements SimpleSelector{

        @Override
        public boolean simpleSelect(Space space) {
            PlayerEntity playerEntity = currentFloor.getPlayer();

            if (space.isOccupied() && space.getOccupant() instanceof Interactable interactibleEntity){
                interactibleEntity.onInteract(playerEntity);
                Dungeon.update(50);
                return true;
            }

            for (Item item : space.getItems()) {
                if (item instanceof Interactable interactibleItem){
                    interactibleItem.onInteract(playerEntity);
                    Dungeon.update(50);
                    return true;
                }
            }
            
            for (Terrain terrain : space.getTerrains()){
                if (terrain instanceof Interactable interactibleTerrain){
                    interactibleTerrain.onInteract(playerEntity);
                    Dungeon.update(50);
                    return true;
                }
            }

            return true;

        }

    }

    private class ExamineSelector implements Selector{

        @Override
        public boolean select(Cursor cursor) {
            if (cursor.getExamined() != null){
                Display.setMenu(new ExamineMenu(getCursor().getExamined()));
            }
            return false;
        }

        @Override
        public boolean canMove(Cursor cursor, Space toSpace) {
            return true;
        }

    }

    private class GetSelector implements SimpleSelector{

        @Override
        public boolean simpleSelect(Space space) {
            Display.setMenu(ItemSelectMenu.createPickupMenu(space, currentFloor.getPlayer()));
            return true;
        }

    }

    private class DropSelector implements SimpleSelector{

        @Override
        public boolean simpleSelect(Space space) {
            Display.setMenu(ItemSelectMenu.createDropMenu(space, currentFloor.getPlayer()));
            return true;
        }

    }

    public class DropDirectSelector implements SimpleSelector {

        private Item item;

        public DropDirectSelector(Item item) {
            this.item = item;
        }

        @Override
        public boolean simpleSelect(Space space) {
            space.addItem(item);
            currentFloor.getPlayer().removeItemFromInventory(item);
            return true;
        }

    }

    public class AimSelector implements Selector {

        private Aimable throwingItem;

        public AimSelector(Aimable throwingItem) {
            this.throwingItem = throwingItem;
        }

        @Override
        public boolean select(Cursor cursor) {
            Space aimingSpace = cursor.getSelectedSpace();
            if (currentFloor.getPlayer().getSpace() == aimingSpace){
                throwingItem.onHit(currentFloor.getPlayer());
                if (throwingItem.landsOnHit()){
                    throwingItem.onLand(currentFloor.getPlayer().getSpace());
                }
            } else {
                List<Space> path = Line.getLineAsListInclusive(currentFloor.getPlayer().getSpace(), aimingSpace);
                path.remove(0);
                Space spawnSpace = path.get(0);
                ThrownItem thrownItem = new ThrownItem(throwingItem, spawnSpace, aimingSpace, 6);
                if (!spawnSpace.isOccupied()){
                    spawnSpace.setOccupant(thrownItem);
                } else {
                    throwingItem.onHit(spawnSpace.getOccupant());
                    if (throwingItem.landsOnHit()){
                        throwingItem.onLand(spawnSpace);
                    }
                }
            }
            if (throwingItem instanceof Item item){
                currentFloor.getPlayer().removeItemFromInventory(item);
            }
            return true;
        }

        @Override
        public boolean canMove(Cursor cursor, Space toSpace) {
            return true;
        }

    }
    
 
}
