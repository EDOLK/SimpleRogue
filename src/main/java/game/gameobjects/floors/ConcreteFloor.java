package game.gameobjects.floors;
import static game.App.lerp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.CheckConditions;
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
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.slots.ItemSlot;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.Terrain;

public class ConcreteFloor implements Floor{

    private final int sizeX;

    private final int sizeY;

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    private Space[][] spaces;
    private PlayerEntity player;

    private Map<Behavable, Integer> timeMap = new WeakHashMap<>();

    private List<BiConsumer<Floor, Floor>> hooks = new ArrayList<>();

    public ConcreteFloor(int SIZE_X, int SIZE_Y, FloorGenerator floorGenerator){
        this(SIZE_X, SIZE_Y, new PlayerEntity(TileColor.transparent(), TileColor.create(255, 255, 255, 255), '@'), floorGenerator);
    }

    public ConcreteFloor(int sizeX, int sizeY, PlayerEntity player, FloorGenerator floorGenerator){

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        spaces = new Space[sizeX][sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                spaces[x][y] = new Space(x, y);
            }
        }
        this.player = player;
        floorGenerator.generateFloor(this, player);
        doLight();

    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public Space getSpace(int x, int y){
        return spaces[x][y];
    }

    public Space getClampedSpace(int x, int y){
        return spaces[clampX(x)][clampY(y)];
    }

    public int clampX(int x){
        return x = x >= getSizeX() ? getSizeX()-1 : (x < 0 ? 0 : x);
    }

    public int clampY(int y){
        return y = y >= getSizeY() ? getSizeY()-1 : (y < 0 ? 0 : y);
    }

    public void attachHook(BiConsumer<Floor,Floor> hook){
        hooks.add(hook);
    }

    public void detachHook(BiConsumer<Floor,Floor> hook){
        hooks.remove(hook);
    }

    public List<BiConsumer<Floor,Floor>> getHooks(){
        return hooks;
    }

    public static class PreppedOverride implements Behavable{
        private OverridesBehavable override;
        private Behavable original;
        public PreppedOverride(OverridesBehavable override, Behavable original){
            this.override = override;
            this.original = original;
        }
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

    public void update(){
        update(100);
    }

    public void update(int time){

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
                        if (slot.getItem() != null && slot.getItem() instanceof Behavable behavableItem){
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

        Map<Space, Integer> lightables = new HashMap<>();

        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                Space space = getSpace(x, y);
                space.setLight(0.0f);
                int l = getLight(space);
                if (l > 0) {
                    lightables.put(space, l);
                }
            }
        }

        lightables.forEach(this::doFloodLight);

    }

    public int getLight(Space space){
        return App.recursiveCheck(space, CheckConditions.all().withInventory(false), (obj) -> Optional.ofNullable(obj instanceof LightSource ls ? ls : null)).stream()
            .mapToInt(LightSource::getLightSourceIntensity)
            .max()
            .orElse(0);
    }

    public void doFloodLight(Space space, int intensity){
        if (intensity <= 0) {
            return;
        }

        Set<Space> litSpaces = new HashSet<>();
        Queue<Space> toBeLitSpaces = new LinkedList<>();
        List<Space> toBeAddedSpaces = new ArrayList<>();

        toBeLitSpaces.add(space);

        do {

            while (!toBeLitSpaces.isEmpty()) {
                Space s = toBeLitSpaces.poll();
                int l = intensity > 10 ? 10 : intensity;
                float light = (float)lerp(0,0,10,1,l);
                s.setLight(Math.max(light, s.getLight()));
                litSpaces.add(s);
                if (!blocksLight(s)){
                    toBeAddedSpaces.addAll(Space.getAdjacentSpaces(s));
                }
                intensity -= s.getTerrains().stream().mapToInt((t) -> t.getLightAbsorption()).sum();
            }

            toBeAddedSpaces.stream()
                .filter((s) -> !litSpaces.contains(s))
                .forEach(toBeLitSpaces::add);

            toBeAddedSpaces.clear();

            intensity--;

        } while (!toBeLitSpaces.isEmpty() && intensity > 0);

    }

    public boolean blocksLight(Space space){
        return (space.isOccupied() && space.getOccupant().isLightBlocker()) || space.getTerrains().stream().anyMatch((t) -> t.isLightBlocker());
    }

}
