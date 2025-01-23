package game.gameobjects.entities;

import static game.Dungeon.getCurrentFloor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hexworks.zircon.api.color.TileColor;

import game.PathConditions;
import game.PathFinder;
import game.PathTracker;
import game.display.Display;
import game.gamelogic.Flammable;
import game.gamelogic.HasDodge;
import game.gamelogic.HasInventory;
import game.gamelogic.OverridesAttack;
import game.gamelogic.OverridesMovement;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.AnimalBehavior;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.behavior.Behavior;
import game.gameobjects.DamageType;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.items.Corpse;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Seperate;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.ExposedTrap;
import game.gameobjects.terrains.Fire;
import game.gameobjects.terrains.HiddenTrap;
import game.gameobjects.terrains.Terrain;

public class Spider extends Animal implements HasDodge, HasInventory{
    
    private ArrayList<Item> inventory = new ArrayList<Item>();
    protected Space nestSpace;
    protected int maxDistance = 12;
    protected int traps;
    protected int maxTraps = 4;

    public Spider(){
        super(TileColor.transparent(), TileColor.create(125, 125, 125, 255), 'x');
        setMaxHP(5);
        setHP(5);
        setWeight(5);
        setName("Giant spider");
        setTileName("Spider");
        setDescription("A giant spider");
        setCorpse(new Corpse(this));
        setBehavior(new Nesting());

        Weapon fangs = new Weapon();
        fangs.setName("fangs");
        fangs.setDamageType(DamageType.PIERCING);
        fangs.setDamage(1, 6);
        setUnarmedWeapon(fangs);
    }

    @Override
    public void defaultInteraction(Entity interactor) {
        Floor.doAttack(interactor, this);
    }

    @Override
    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public int getMaxWeight() {
        return 50;
    }

    @Override
    public int getDodge() {
        return 10;
    }

    protected int getDistanceFromNest(){
        return Math.max(Math.abs(this.getX() - this.nestSpace.getX()), Math.abs(this.getY() - this.nestSpace.getY()));
    }


    /**
     * Nesting
     */
    protected class Nesting extends Behavior{

        protected Nesting(){}

        @Override
        public void behave() {
            nestSpace = getSpace();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    Space pSpace = getCurrentFloor().getSpace(getX()+i, getY()+j);
                    if (!pSpace.isOccupied() || pSpace.getOccupant() == Spider.this){
                        pSpace.addTerrain(new Web());
                    }
                }
            }
            setBehavior(new Trapping());
        }
        
    }

    /**
     * Trapping
     */
    protected class Trapping extends AnimalBehavior{
        
        protected PathFinder pathFinder;
        
        protected Trapping(){
            super(Spider.this);
        }

        @Override
        protected int getXMax() {
            return getCurrentFloor().clampX(Spider.this.nestSpace.getX() + Spider.this.maxDistance);
        }

        @Override
        protected int getXMin() {
            return getCurrentFloor().clampX(Spider.this.nestSpace.getX() - Spider.this.maxDistance);
        }

        @Override
        protected int getYMax() {
            return getCurrentFloor().clampY(Spider.this.nestSpace.getY() + Spider.this.maxDistance);
        }

        @Override
        protected int getYMin() {
            return getCurrentFloor().clampY(Spider.this.nestSpace.getY() - Spider.this.maxDistance);
        }

        @Override
        public void behave() {
            wander();
        }

        @Override
        protected boolean spaceIsInvalid(Space space) {
            return super.spaceIsInvalid(space) || !space.getTerrains().isEmpty();
        }

        @Override
        protected void wander() {
            if (Spider.this.traps < Spider.this.maxTraps) {
                super.wander();
                if (wanderingPathTracker != null && wanderingPathTracker.pathIsDone()) {
                    Spider.this.getSpace().addTerrain(new Web());
                    Spider.this.traps++;
                }
            } else {
                setBehavior(new Waiting());
            }
        }

        @Override
        protected PathConditions generateConditionsToEntity() {
            return generateConditionsToSpace();
        }

        @Override
        protected PathConditions generateConditionsToSpace() {
            return new PathConditions().addDeterrentConditions(
                space -> {
                    for (Terrain terrain : space.getTerrains()) {
                        if (!(terrain instanceof Web)){
                            return 10d;
                        }
                    }
                    return 0d;
                }
            );
        }

        
    }

    /**
     * Waiting
     */
    protected class Waiting extends AnimalBehavior{

        protected PathTracker tracker;
        
        public Waiting(){
            super(Spider.this);
        }

        @Override
        public void behave() {
            if (Spider.this.getSpace() != Spider.this.nestSpace) {
                if (tracker != null && tracker.nextSpaceAvailable()) {
                    Space.moveEntity(Spider.this, tracker.getNext());
                } else {
                    Optional<PathTracker> t = PathTracker.createPathTracker(Spider.this, Spider.this.nestSpace, generateConditionsToSpace());
                    if (t.isPresent()) {
                        tracker = t.get();
                        behave();
                        return;
                    }
                }
            } else {
                Entity prey = checkForTarget();
                if (prey != null && isAdjacent(prey)) {
                    Hunting h = new Hunting(prey);
                    Spider.this.setBehavior(h);
                    h.behave();
                    return;
                }
            }
        }
    
    }
    
    /**
     * Hunting
     */
    protected class Hunting extends AnimalBehavior {

        protected Hunting(Entity target){
            super(Spider.this);
            this.target = target;
            Optional<PathTracker> t = PathTracker.createPathTracker(Spider.this, target, generateConditionsToEntity());
            if (t.isPresent()) {
                this.huntingPathTracker = t.get();
            }
        }

        @Override
        public void behave() {
            if (getEntitiesInVision().contains(target) && getDistanceFromNest() < Spider.this.maxDistance) {
                if (huntingPathTracker != null && huntingPathTracker.nextSpaceAvailable()) {
                    Space nextSpace = huntingPathTracker.getNext();
                    if (!Space.moveEntity(Spider.this, nextSpace) && nextSpace.isOccupied() && nextSpace.getOccupant() == target){
                        Floor.doAttack(Spider.this,target);
                    }
                    return;
                } else {
                    Optional<PathTracker> tracker = PathTracker.createPathTracker(Spider.this, target, generateConditionsToEntity());
                    if (tracker.isPresent()) {
                        this.huntingPathTracker = tracker.get();
                        behave();
                        return;
                    }
                }
            }
            wander();
        }

        

        @Override
        public void wander() {
            Behavior behavior = new Waiting();
            if (Spider.this.traps < Spider.this.maxTraps)
                behavior = new Trapping();
            setBehavior(behavior);
            behavior.behave();
        }

    }
    
    /**
     * Web
     */
    public class Web extends ExposedTrap implements SelfAware, Flammable{
        
        protected String name;
        protected String description;
        protected Space space;
        protected boolean t = false;
        
        public Web(){
            setCharacter('#');
            setfGColor(TileColor.create(200, 200, 200, 255));
            setbGColor(TileColor.transparent());
            setTileName("Web");
            this.name = "Webs";
            this.description = "Spider webs.";
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public void triggerOnEntity(Entity entity) {
            if (!(entity instanceof Spider)){
                if (entity instanceof PlayerEntity){
                    Display.log("You get stuck in the webs!");
                } else{
                    Display.log("The" + entity.getName() + " gets stuck in the webs!", entity.getSpace());
                }
                t = true;
                entity.addStatus(new Webbed(5));
                getSpace().removeTerrain(this);
                Spider.this.traps--;
                if (!(Spider.this.getBehavior() instanceof Hunting)){
                    if (Spider.this.getDistanceFromNest() < Spider.this.maxDistance){
                        Spider.this.setBehavior(new Hunting(entity));
                    }
                }
            } else {
                t = false;
            }
        }

        @Override
        public boolean triggerableWhenAdding() {
            return false;
        }

        @Override
        public Space getSpace() {
            return space;
        }

        @Override
        public void setSpace(Space space) {
            this.space = space;
        }

        @Override
        public boolean isDestroyedOnTrigger() {
            return t;
        }

        @Override
        public int getFuelValue() {
            return 1;
        }

        @Override
        public void onBurn(Fire fire) {
            getSpace().remove(this);
        }
        
    }
    /**
     * HiddenWeb
     */
    public class HiddenWeb extends HiddenTrap{

        public HiddenWeb(Web web) {
            super(web);
        }
        
    }
    
    /**
     * Webbed
     */
    public static class Webbed extends Status implements OverridesAttack, OverridesMovement, Behavable, Seperate{

        private int turns;

        public Webbed(int turns){
            this.turns = turns;
            setCharacter('░');
            setbGColor(TileColor.transparent());
            setfGColor(TileColor.create(255, 255, 255, 255));
            setDescriptor("Webbed");
            setTileName("Web");
        }

        @Override
        public boolean overrideMovement(Entity entity, Space toSpace) {
            if (entity instanceof PlayerEntity){
                Display.log("You struggle against the webs.");
            } else {
                Display.log("The " + entity.getName() + " struggles against the webs", entity.getSpace());
            }
            return false;
        }

        @Override
        public void overrideAttack(Entity attacker, Entity attackee) {
            overrideMovement(attacker, null);
        }

        @Override
        public void behave() {
            turns--;
            if (turns <= 0){
                owner.removeStatus(this);
            }
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public void onStack(Status SameStatus) {
            if (SameStatus instanceof Webbed webbed) {
                webbed.turns += this.turns;
            }
        }

        @Override
        public Status validateSameness(List<Status> Statuses) {
            for (Status status : Statuses) {
                if (status instanceof Webbed webbed){
                    return webbed;
                }
            }
            return null;
        }
        
    }

    
}
