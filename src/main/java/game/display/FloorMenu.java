package game.display;

import static game.App.lerp;
import static game.gameobjects.Space.moveEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.hexworks.zircon.api.ComponentDecorations;
import org.hexworks.zircon.api.builder.component.HeaderBuilder;
import org.hexworks.zircon.api.builder.component.LabelBuilder;
import org.hexworks.zircon.api.builder.component.LogAreaBuilder;
import org.hexworks.zircon.api.builder.component.PanelBuilder;
import org.hexworks.zircon.api.builder.component.ParagraphBuilder;
import org.hexworks.zircon.api.builder.component.ProgressBarBuilder;
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
import org.hexworks.zircon.api.graphics.LayerHandle;
import org.hexworks.zircon.api.graphics.StyleSet;
import org.hexworks.zircon.api.uievent.KeyboardEvent;
import org.hexworks.zircon.api.uievent.UIEventPhase;
import org.hexworks.zircon.api.uievent.UIEventResponse;

import game.App;
import game.CheckConditions;
import game.Dungeon;
import game.display.Display.Mode;
import game.display.KeyMap.Action;
import game.gamelogic.Experiential;
import game.gamelogic.Levelable;
import game.gamelogic.OverridesPlayerInput;
import game.gamelogic.combat.Attack;
import game.gamelogic.floorinteraction.DropSelector;
import game.gamelogic.floorinteraction.ExamineSelector;
import game.gamelogic.floorinteraction.GetSelector;
import game.gamelogic.floorinteraction.InteractSelector;
import game.gamelogic.floorinteraction.SelectionResult;
import game.gamelogic.floorinteraction.Selector;
import game.gamelogic.floorinteraction.SimpleSelector;
import game.gameobjects.DisplayableTile;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Door;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.entities.Wall;
import game.gameobjects.items.Item;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.OpenDoor;
import game.gameobjects.terrains.Staircase;
import game.gameobjects.terrains.Terrain;

public final class FloorMenu extends Menu{

    private LayerHandle memoryLayer;
    private List<LayerHandle> layers = new ArrayList<>();
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
            .withPreferredSize(currentFloor.SIZE_X,1)
            .build();

        screen.addComponent(examineName);
        
        update();
    }

    public StyleSet getLogStyleSet(){
        return logMessageArea.getCurrentStyle();
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
        layers.forEach((lh) -> lh.removeLayer());
        layers.clear();

        for (Space space : playerEntity.getSpacesInVision(true)){
            memoryLayer.draw(Tile.empty(), Position.create(space.getX(), space.getY()));
            addToLayers(space, playerEntity);
        }

        updatePlayerStatus(playerEntity);
        drawCursor(playerEntity);

    }

    public void draw(Tile tile, Position position){
        if (tile == null || tile == Tile.empty()) {
            return;
        }
        for (LayerHandle layerHandle : layers) {
            Tile t = layerHandle.getTileAtOrNull(position);
            if (t == null) {
                layerHandle.draw(tile, position);
                return;
            }
        }
        LayerHandle layer = screen.addLayer(Layer.newBuilder().withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y).build());
        layer.draw(tile, position);
        layers.add(layer);
    }

    public void toggleMemory(){
        memoryLayer.setHidden(!memoryLayer.isHidden());
    }

    private void addToLayers(Space current, PlayerEntity playerEntity){
        int x = current.getX();
        int y = current.getY();
        float darkness = 1.0f - current.getLight();
        float darknessCeil = 0.75f;

        darkness = Math.min(darkness, darknessCeil);

        draw(current.getTile(darkness), Position.create(x,y));

        for (Terrain terrain : current.getTerrains()) {
            if (terrain instanceof OpenDoor || terrain instanceof Staircase)
                drawInMemory(terrain, Position.create(x,y));
            draw(terrain.getTile((double) darkness), Position.create(x, y));
        }

        for (Item item : current.getItems())
            draw(item.getTile(darkness), Position.create(x, y));

        if (current.isOccupied())
            drawEntity(current.getOccupant(), x, y, darkness);

        if (Display.getMode() == Mode.GRAPHICAL){
            int darkValue = (int)(darkness * 10);
            Tile darknessTile = Tile.newBuilder()
                .withName("Darkness " + darkValue)
                .withTileset(Display.getGraphicalTileSet())
                .buildGraphicalTile();
            draw(darknessTile, Position.create(x, y));
        }
    }

    private void drawCursor(PlayerEntity playerEntity) {
        if (cursor != null){
            Space cursorSpace = cursor.getSelectedSpace();
            draw(cursor.getTile(), Position.create(cursorSpace.getX(), cursorSpace.getY()));
            if (
                (cursor.getSelectedSpace().getLight() > 0 && playerEntity.isWithinVision(cursorSpace)) || 
                (playerEntity.isWithinVision(cursorSpace) && Space.manDist(playerEntity.getSpace(), cursorSpace) <= playerEntity.getNightVisionRange())
            ) {
                cursor.collectExaminables();
                setExamineTooltip();
            }
        }
    }

    private void drawEntity(Entity occupant, int x, int y, double darkness) {

        if (occupant instanceof Wall || occupant instanceof Door)
            drawInMemory(occupant, Position.create(x,y));

        draw(occupant.getTile(darkness), Position.create(x, y));

        for (Status status : occupant.getStatuses()) {
            draw(status.getTile(status.isFullBright() ? 0.0 : darkness), Position.create(x + status.getxOffset(), y + status.getyOffset()));
        }

        if (Display.getMode() == Mode.GRAPHICAL && occupant.getHP() < occupant.getMaxHP()){
            int healthBarValue = (int)lerp(0, 7, occupant.getMaxHP(), 1, occupant.getHP());
            Tile healthBar = Tile.newBuilder()
                .withName("Health Bar " + healthBarValue)
                .withTileset(Display.getGraphicalTileSet())
                .buildGraphicalTile();
            draw(healthBar, Position.create(x, y));
        }

    }

    public void drawInMemory(DisplayableTile tile, Position position) {
        if (Display.getMode() == Mode.ASCII){
            int blue = tile.getTile().getForegroundColor().getBlue() * 2;
            memoryLayer.draw(
                tile.getTile().withForegroundColor(tile.getTile().getForegroundColor().withBlue(blue).darkenByPercent(.85)),
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
        logMessageArea.addParagraph(message, false, 0l);
    }
    
    public void addToLog(String message, Space space){
        if (currentFloor.getPlayer().isWithinVision(space)){
            addToLog(message);
        }
    }

    public void addToLog(ParagraphBuilder paragraph){
        logMessageArea.addParagraph(paragraph, false);
    }

    public void addToLog(ParagraphBuilder paragraph, Space space){
        if (currentFloor.getPlayer().isWithinVision(space)){
            addToLog(paragraph);
        }
    }

    public void addToLog(String message, Function<StyleSet, StyleSet> styleTransformer, Space space){
        if (currentFloor.getPlayer().isWithinVision(space)){
            addToLog(message, styleTransformer);
        }
    }

    public void addToLog(String message, Function<StyleSet, StyleSet> styleTransformer){
        StyleSet style = logMessageArea.getCurrentStyle();
        style = styleTransformer.apply(style);
        ParagraphBuilder paragraph = ParagraphBuilder.newBuilder()
            .withText(message)
            .withComponentStyleSet(Display.composeComponentStyleSet(style));
        addToLog(paragraph);
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
            .withPreferredSize(55, 10)
            .build();
        
        logMessageArea = LogAreaBuilder.newBuilder()
            .withPosition(0, 0)
            .withPreferredSize(logPanel.getWidth()-2, logPanel.getHeight()-2)
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
            .withPreferredSize(20,20)
            .build();

        Label hpLabel = LabelBuilder.newBuilder()
            .withText("HP:")
            .build();
        
        statusPanel.addComponent(hpLabel);

        hpText = HeaderBuilder.newBuilder()
            .withPosition(Position.create(3, 0))
            .withPreferredSize(7, 1)
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
            .withPreferredSize(7,1)
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
            .withPreferredSize(3,1)
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
            .withPreferredSize(6,1)
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
            .withPreferredSize(3,1)
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
            .withPreferredSize(7,1)
            .build();

        statusPanel.addComponent(weightText);

        timeText = HeaderBuilder.newBuilder()
            .withPosition(0,6)
            .withPreferredSize(7,1)
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
            .withPreferredSize(20,20)
            .build();

        this.nameHeader = HeaderBuilder.newBuilder()
            .withPreferredSize(this.enemyPanel.getWidth()-2,1)
            .withPosition(0,3)
            .build();
        this.enemyPanel.addComponent(this.nameHeader);

        this.enemyBar = ProgressBarBuilder.newBuilder()
            .withPosition(3,1)
            .withPreferredSize(this.enemyPanel.getWidth()-5,1)
            .withNumberOfSteps(this.enemyPanel.getWidth()-5)
            .build();
        this.enemyPanel.addComponent(this.enemyBar);

        this.enemyHpHeader = HeaderBuilder.newBuilder()
            .withPreferredSize(this.enemyBar.getWidth(),1)
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

        memoryLayer = screen.addLayer(Layer.newBuilder().withSize(currentFloor.SIZE_X, currentFloor.SIZE_Y).build());
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
        // TODO: gross but necessary, find better way to do this later.
        if (!currentFloor.getPlayer().isAlive())
            currentState = State.DEAD;

        return response;
    }
    

    public UIEventResponse handleInGame(KeyboardEvent event, UIEventPhase phase){
        List<OverridesPlayerInput> overridesInput = App.recursiveCheck(
            currentFloor.getPlayer(),
            CheckConditions.none().withStatuses(true),
            (obj -> obj instanceof OverridesPlayerInput op ? Optional.of(op) : Optional.empty())
        );
        if (overridesInput.size() > 0) {
            return overridesInput.get(0).handleKeyboardEvent(event, phase);
        }
        int time = -1;
        switch (Display.getKeyMap().getAction(event.getCode())) {
            case UP: //up
                if (event.getCtrlDown()){
                    time = forceHit(0, -1);
                    break;
                }
                time = tryMoveToAdjacent(0, -1);
                break;
            case DOWN: //down
                if (event.getCtrlDown()){
                    time = forceHit(0, 1);
                    break;
                }
                time = tryMoveToAdjacent(0, 1);
                break;
            case LEFT: //left
                if (event.getCtrlDown()){
                    time = forceHit(-1, 0);
                    break;
                }
                time = tryMoveToAdjacent(-1, 0);
                break;
            case RIGHT: //right
                if (event.getCtrlDown()){
                    time = forceHit(1, 0);
                    break;
                }
                time = tryMoveToAdjacent(1, 0);
                break;
            case UP_LEFT: //up-left
                if (event.getCtrlDown()){
                    time = forceHit(-1, -1);
                    break;
                }
                time = tryMoveToAdjacent(-1, -1);
                break;
            case UP_RIGHT: //up-right
                if (event.getCtrlDown()){
                    time = forceHit(1, -1);
                    break;
                }
                time = tryMoveToAdjacent(1, -1);
                break;
            case DOWN_LEFT: //down-left
                if (event.getCtrlDown()){
                    time = forceHit(-1, 1);
                    break;
                }
                time = tryMoveToAdjacent(-1, 1);
                break;
            case DOWN_RIGHT: //down-right
                if (event.getCtrlDown()){
                    time = forceHit(1, 1);
                    break;
                }
                time = tryMoveToAdjacent(1, 1);
                break;
            case CENTER: //wait
                addToLog("waiting...");
                time = currentFloor.getPlayer().getTimeToWait();
                break;
            case INTERACT_TOGGLE: //Interact
                startSelecting(new InteractSelector(currentFloor.getPlayer()));
                break;
            case ESCAPE: //pause
                Display.setMenu(new PauseMenu());
                break;
            case EXAMINE_TOGGLE: //examining
                startSelecting(new ExamineSelector());
                break;
            case GET_TOGGLE: //getting
                startSelecting(new GetSelector(currentFloor.getPlayer()));
                break;
            case EQUIPMENT: //equiping
                Display.setMenu(EquipmentMenu.createEquipEquipmentMenu(currentFloor.getPlayer()));
                break;
            case DROP_TOGGLE: //dropping
                startSelecting(new DropSelector(currentFloor.getPlayer()));
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
            case SKILL_TREES:
                Display.setMenu(new SkillTreesMenu(currentFloor.getPlayer()));
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

    public int tryMoveToAdjacent(int toX, int toY){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return -1;
        }
        PlayerEntity playerEntity = currentFloor.getPlayer();
        int x = playerEntity.getX();
        int y = playerEntity.getY();
        Space potentialSpace = currentFloor.getSpace(x+toX, y+toY);
        if (potentialSpace.isOccupied()){
            return potentialSpace.getOccupant().defaultInteraction(playerEntity);
        } else {
            return moveEntity(playerEntity, potentialSpace).isSuccessful() ? playerEntity.getTimeToMove() : playerEntity.getTimeToWait();
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
            Attack.doAttack(playerEntity, entity);
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
                SelectionResult result = selector.select(getCursor());
                if (result.isSubmitted()) {
                    toggleExamination();
                    currentState = State.INGAME;
                    if (result.getTimeTaken() > 0) {
                        Dungeon.update(result.getTimeTaken());
                    }
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
        SelectionResult result = null;
        switch (Display.getKeyMap().getAction(event.getCode())) {
            case ESCAPE:
                currentState = State.INGAME;
                break;
            case CENTER:
                result = attemptSelect(0, 0, simpleSelector);
                break;
            case UP: //up
                result = attemptSelect(0, -1, simpleSelector);
                break;
            case DOWN: //down
                result = attemptSelect(0, 1, simpleSelector);
                break;
            case LEFT: //left
                result = attemptSelect(-1, 0, simpleSelector);
                break;
            case RIGHT: //right
                result = attemptSelect(1, 0, simpleSelector);
                break;
            case UP_LEFT: //up-left
                result = attemptSelect(-1, -1, simpleSelector);
                break;
            case UP_RIGHT: //up-right
                result = attemptSelect(1, -1, simpleSelector);
                break;
            case DOWN_LEFT: //down-left
                result = attemptSelect(-1, 1, simpleSelector);
                break;
            case DOWN_RIGHT: //down-right
                result = attemptSelect(1, 1, simpleSelector);
                break;
            default:
                break;
        }
        if (result != null && result.isSubmitted()) {
            currentState = State.INGAME;
            if (result.getTimeTaken() > 0) {
                Dungeon.update(result.getTimeTaken());
            }
            Display.update();
        }
        return UIEventResponse.processed();
    }

    private SelectionResult attemptSelect(int toX, int toY, SimpleSelector simpleSelector){
        if (toX < -1 || toX > 1 || toY < -1 || toY > 1){
            return new SelectionResult(false, 0);
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
 
}
