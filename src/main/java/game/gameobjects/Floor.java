package game.gameobjects;
import static game.App.lerp;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import org.hexworks.zircon.api.color.TileColor;

import game.Line;
import game.floorgeneration.FloorGenerator;
import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.HasOffHand;
import game.gamelogic.LightSource;
import game.gamelogic.OverridesBehavable;
import game.gamelogic.abilities.Ability;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.abilities.HasPassives;
import game.gamelogic.abilities.Passive;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.Terrain;

public class Floor{

    public final int SIZE_X;
    public final int SIZE_Y;

    private Space[][] spaces;
    private PlayerEntity player;

    private Map<Behavable, Integer> timeMap = new WeakHashMap<>();
    private int lastTime = 0;

    public Floor(int SIZE_X, int SIZE_Y, FloorGenerator floorGenerator){
        this(SIZE_X, SIZE_Y, new PlayerEntity(TileColor.transparent(), TileColor.create(255, 255, 255, 255), '@'), floorGenerator);
    }

    public Floor(int SIZE_X, int SIZE_Y, PlayerEntity player, FloorGenerator floorGenerator){

        this.SIZE_X = SIZE_X;
        this.SIZE_Y = SIZE_Y;
        spaces = new Space[SIZE_X][SIZE_Y];
        this.player = player;
        floorGenerator.generateFloor(spaces, player);
        doLight();

    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public Space[][] getSpaces() {
        return spaces;
    }

    public Space getSpace(int x, int y){
        return spaces[x][y];
    }

    public Space getClampedSpace(int x, int y){
        return spaces[clampX(x)][clampY(y)];
    }

    public int clampX(int x){
        return x = x >= SIZE_X ? SIZE_X-1 : (x < 0 ? 0 : x);
    }

    public int clampY(int y){
        return y = y >= SIZE_Y ? SIZE_Y-1 : (y < 0 ? 0 : y);
    }

    public int getLastTime() {
        return lastTime;
    }

    public void update(){
        update(100);
    }

    public static class PreppedOverride implements Behavable{
        private OverridesBehavable override;
        private Behavable original;
        private PreppedOverride(OverridesBehavable override, Behavable original){
            this.override = override;
            this.original = original;
        }
        private PreppedOverride(){}
        public OverridesBehavable getOverride() {
            return override;
        }
        public Behavable getOriginal() {
            return original;
        }
        @Override
        public int behave() {
            return override.overrideBehave(original);
        }
        @Override
        public boolean isActive() {
            return override.overrideIsActive(original);
        }
    }

    public void update(int time){

        this.lastTime = time;

        Stack<Behavable> behavables = new Stack<Behavable>();

        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                Space currentSpace = getSpace(x, y);

                if (currentSpace.isOccupied()){
                    Entity entity = currentSpace.getOccupant();

                    Behavable bEntity = null;

                    if (entity instanceof Behavable behavableEntity){
                        bEntity = behavableEntity;
                    }

                    for (Status status : entity.getStatuses()) {
                        if (status instanceof Behavable behavableStatus){
                            behavables.add(behavableStatus);
                        }
                        if (status instanceof OverridesBehavable ovBehavable && bEntity != null){
                            bEntity = new PreppedOverride(ovBehavable, bEntity);
                        }
                    }

                    if (bEntity != null) {
                        behavables.add(bEntity);
                    }

                    if (entity instanceof HasAbilities hasAbilities) {
                        for (Ability ability : hasAbilities.getAbilities()){
                            if (ability instanceof Behavable behavableAbility){
                                behavables.add(behavableAbility);
                            }
                        }
                    }

                    if (entity instanceof HasPassives hasPassives) {
                        for (Passive passive : hasPassives.getPassives()){
                            if (passive instanceof Behavable behavablePassive){
                                behavables.add(behavablePassive);
                            }
                        }
                    }

                    if (entity instanceof HasOffHand hasOffHand){
                        ItemSlot slot = hasOffHand.getOffHandSlot();
                        if (slot.getEquippedItem() != null && slot.getEquippedItem() instanceof Behavable behavableItem){
                            behavables.add(behavableItem);
                        }
                    }

                    if (entity instanceof Armed armedOccupant){
                        for (Weapon weapon : armedOccupant.getWeapons()) {
                            if (weapon instanceof Behavable behavableWeapon) {
                                behavables.add(behavableWeapon);
                            }
                        }
                    }

                    if (entity instanceof Armored armoredOccupant){
                        for (Armor armor : armoredOccupant.getArmor()) {
                            if (armor instanceof Behavable behavableArmor) {
                                behavables.add(behavableArmor);
                            }
                        }
                    }

                }

                for (Item item : currentSpace.getItems()) {
                    if (item instanceof Behavable behavableItem){
                        behavables.add(behavableItem);
                    }
                }

                for (Terrain terrain : currentSpace.getTerrains()) {
                    if (terrain instanceof Behavable behavableTerrain){
                        behavables.add(behavableTerrain);
                    }
                }

            }
        }

        while (!behavables.isEmpty()) {

            Behavable behavable = behavables.pop();

            int timeToBehave = time;

            if (timeMap.containsKey(behavable)) {
                timeToBehave -= timeMap.get(behavable);
            }

            while (timeToBehave > 0) {
                if (!behavable.isActive()) {
                    break;
                }
                timeToBehave -= behavable.behave();
            }

            if (timeToBehave < 0) {
                timeMap.put(behavable, Math.abs(timeToBehave));
            }

            if (timeToBehave == 0){
                timeMap.remove(behavable);
            }

        }

        doLight();

    }

    public void doLight() {

        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                getSpace(x, y).setLight(0.0f);
            }
        }

        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                doLightRevised(spaces[x][y]);
            }
        }

        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                Space space = spaces[x][y];
                if (space.getLight() > 0){
                    doPreLineLight(space, (int)(space.getLight()*10));
                }
            }
        }

    }

    public void doLightRevised(Space space){
        LightSource strongestLightSource = null;
        int intensity = 0;
        for (Item item : space.getItems()) {
            strongestLightSource = calculateLightSource(strongestLightSource, item);
        }
        for (Terrain terrain : space.getTerrains()) {
            strongestLightSource = calculateLightSource(strongestLightSource, terrain);
        }
        if (space.isOccupied()){
            Entity occupant = space.getOccupant();
            strongestLightSource = calculateLightSource(strongestLightSource, occupant);
            for (Status status : occupant.getStatuses()) {
                strongestLightSource = calculateLightSource(strongestLightSource, status);
            }
            if (occupant instanceof Armed armedOccupant){
                for (Weapon weapon : armedOccupant.getWeapons()) {
                    strongestLightSource = calculateLightSource(strongestLightSource, weapon);
                }
            }
            if (occupant instanceof Armored armoredOccupant){
                for (Armor armor : armoredOccupant.getArmor()) {
                    strongestLightSource = calculateLightSource(strongestLightSource, armor);
                }
            }
            if (occupant instanceof HasOffHand hasOffHand && hasOffHand.getOffHandSlot().getEquippedItem() != null){
                strongestLightSource = calculateLightSource(strongestLightSource, hasOffHand.getOffHandSlot().getEquippedItem());
            }
        }

        intensity = strongestLightSource != null ? strongestLightSource.getLightSourceIntensity() : 0;

        doPreLineLight(space, intensity);
    }

    private void doPreLineLight(Space space, int intensity) {
        int minYDiff = clampY(space.getY() - intensity);
        int maxYDiff = clampY(space.getY() + intensity);

        int minXDiff = clampX(space.getX() - intensity);
        int maxXDiff = clampX(space.getX() + intensity);

        for (int i = -intensity; i <= intensity; i++) {
            Space querySpace;

            querySpace = getClampedSpace(space.getX() + i, minYDiff);
            doLineLight(space, intensity, querySpace);

            querySpace = getClampedSpace(space.getX() + i, maxYDiff);
            doLineLight(space, intensity, querySpace);

            if (i == -intensity || i == intensity){
                continue;
            }

            querySpace = getClampedSpace(minXDiff, space.getY() + i);
            doLineLight(space, intensity, querySpace);

            querySpace = getClampedSpace(maxXDiff, space.getY() + i);
            doLineLight(space, intensity, querySpace);
        }
    }

    private void doLineLight(Space fromSpace, int intensity, Space toSpace) {
        List<Space> lineList = Line.getLineAsListInclusive(fromSpace, toSpace, spaces);
        for (int i = 0; i < lineList.size(); i++) {
            Space space = lineList.get(i);
            int j = intensity - i;
            j = j > 10 ? 10 : j;
            if (j <= 0)
                return;
            float light = (float)lerp(0,0,10,1,j);
            if (space.getLight() < light)
                space.setLight(light);
            if (space.isOccupied() && space.getOccupant().isLightBlocker())
                return;
        }
    }

    private LightSource calculateLightSource(LightSource strongestLightSource, Object object) {
        if (object instanceof LightSource lightSource){
            if (strongestLightSource == null){
                strongestLightSource = lightSource;
            } else if (strongestLightSource.getLightSourceIntensity() < lightSource.getLightSourceIntensity()){
                strongestLightSource = lightSource;
            }
        }
        return strongestLightSource;
    }

}
