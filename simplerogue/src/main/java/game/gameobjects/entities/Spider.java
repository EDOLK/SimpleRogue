package game.gameobjects.entities;

import static game.Dungeon.getCurrentFloor;
import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.PathFinder;
import game.display.Display;
import game.gamelogic.Flammable;
import game.gamelogic.HasDodge;
import game.gamelogic.HasInventory;
import game.gamelogic.OverridesAttack;
import game.gamelogic.OverridesMovement;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.behavior.Behavior;
import game.gameobjects.DamageType;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.items.Corpse;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.ExposedTrap;
import game.gameobjects.terrains.Fire;
import game.gameobjects.terrains.HiddenTrap;

public class Spider extends Entity implements HasDodge, HasInventory{
    
    private ArrayList<Item> inventory = new ArrayList<Item>();
    protected Space nestSpace;
    protected int maxDistance = 12;
    protected List<Space> trappedSpaces = new ArrayList<Space>();
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
                    try {
                        Space pSpace = getCurrentFloor().getSpace(getX()+i, getY()+j);
                        if (!pSpace.isOccupied() || pSpace.getOccupant() == Spider.this){
                            pSpace.addTerrain(new Web());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
            setBehavior(new Trapping());
        }
        
    }

    /**
     * Trapping
     */
    protected class Trapping extends Behavior{
        
        protected PathFinder pathFinder;
        
        protected Trapping(){}

        protected Space getEmptyViableSpace(){
            int yMin = Spider.this.nestSpace.getY() - Spider.this.maxDistance;
            int yMax = Spider.this.nestSpace.getY() + Spider.this.maxDistance;
            int xMin = Spider.this.nestSpace.getX() - Spider.this.maxDistance;
            int xMax = Spider.this.nestSpace.getX() + Spider.this.maxDistance;
            if (yMin < 0){
                yMin = 0;
            }
            if (yMax >= getCurrentFloor().SIZE_Y){
                yMax = getCurrentFloor().SIZE_Y-1;
            }
            if (xMin < 0){
                xMin = 0;
            }
            if (xMax >= getCurrentFloor().SIZE_X){
                xMax = getCurrentFloor().SIZE_X-1;
            }
            Space possibleSpace = getCurrentFloor().getSpace(randomNumber(xMin, xMax), randomNumber(yMin, yMax));
            while (possibleSpace.isOccupied()) {
                possibleSpace = getCurrentFloor().getSpace(randomNumber(xMin, xMax), randomNumber(yMin, yMax));
            }
            return possibleSpace;
        }

        @Override
        public void behave() {
            if (Spider.this.trappedSpaces.size() >= Spider.this.maxTraps){
                Waiting w = new Waiting();
                Spider.this.setBehavior(w);
                w.behave();
                return;
            }
            if (pathFinder != null){
                Space nextSpace = pathFinder.getSpace();
                if (Space.moveEntity(Spider.this, nextSpace)){
                    if (!pathFinder.pathHasEnded()){
                        pathFinder.iterate();
                    } else {
                        nextSpace.addTerrain(new Web());
                        Spider.this.trappedSpaces.add(nextSpace);
                        pathFinder = null;
                    }
                } else {
                    pathFinder = null;
                }
            } else {
                Space querySpace = getEmptyViableSpace();
                if (!Spider.this.trappedSpaces.contains(querySpace)){
                    if (pathFinder == null){
                        try {
                            pathFinder = new PathFinder(Spider.this.getSpace(), querySpace);
                            pathFinder.iterate();
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }
    }

    /**
     * Waiting
     */
    protected class Waiting extends Behavior{

        private PathFinder pathFinder;
        
        public Waiting(){}

        @Override
        public void behave() {
            if (pathFinder != null && Spider.this.getSpace() != Spider.this.nestSpace){
                if(Space.moveEntity(Spider.this, pathFinder.getSpace()) && !pathFinder.pathHasEnded()){
                    pathFinder.iterate();
                } else {
                    try {
                        pathFinder = new PathFinder(Spider.this.getSpace(), Spider.this.nestSpace);
                        pathFinder.iterate();
                    } catch (Exception e) {

                    }
                }
            } else if (Spider.this.getSpace() != Spider.this.nestSpace){
                try {
                    pathFinder = new PathFinder(Spider.this.getSpace(), Spider.this.nestSpace);
                    pathFinder.iterate();
                } catch (Exception e) {

                }
            } else {
                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        try {
                            Space querySpace = getCurrentFloor().getSpace(Spider.this.getX() + i, Spider.this.getY() + j);
                            if (querySpace.isOccupied() && querySpace.getOccupant() instanceof PlayerEntity playerEntity){
                                Spider.this.setBehavior(new Hunting(playerEntity));
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }
            }
        }
    
    }
    
    /**
     * Hunting
     */
    protected class Hunting extends Behavior {
        
        private Entity target;

        protected Hunting(Entity target){
            this.target = target;
        }

        @Override
        public void behave() {
            int dist = (Math.abs(Spider.this.getX() - Spider.this.nestSpace.getX()) + Math.abs(Spider.this.getY() - Spider.this.nestSpace.getY()))/2;
            if (dist >= Spider.this.maxDistance){
                Spider.this.setBehavior(new Waiting());
                return;
            }
            try {
                PathFinder p = new PathFinder(Spider.this.getSpace(), target.getSpace());
                if (!Space.moveEntity(Spider.this, p.getNext())){
                    if (p.getSpace().getOccupant() == target){
                        Floor.doAttack(Spider.this, target);
                    }
                }
            } catch (Exception e) {

            }
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
                if (!(Spider.this.getBehavior() instanceof Hunting)){
                    if (getDistance() < Spider.this.maxDistance){
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
        
        public int getDistance(){
            return (Math.abs(getSpace().getX() - Spider.this.nestSpace.getX()) + Math.abs(getSpace().getY() - Spider.this.nestSpace.getY()))/2;
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
    public static class Webbed extends Status implements OverridesAttack, OverridesMovement, Behavable{

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
        
    }

    
}
