package game.gameobjects.entities;

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

public class Spider extends Animal implements HasDodge, HasInventory{
    
    private ArrayList<Item> inventory = new ArrayList<Item>();
    protected Space nestSpace;
    protected List<Space> trappedSpaces = new ArrayList<Space>();
    protected int maxTraps = 4;

    public Spider(){
        super(TileColor.transparent(), TileColor.create(125, 125, 125, 255), 'x');
        setMaxHP(5);
        setHP(5);
        setWeight(5);
        setName("Giant spider");
        setTileName("Cave spider");
        setDescription("A giant spider");
        setCorpse(new Corpse(this));
        setBehavior(new Wandering(this));

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
    private static class Nesting extends Behavior{

        private Spider spider;
        private PathFinder pathFinder;

        public Nesting(Spider spider){
            this.spider = spider;
        }

        @Override
        public void behave() {
        }
        
    }

    private static class Wandering extends Behavior{
        
        private Spider spider;
        private Space wanderSpace;
        private PathFinder pathFinder;
        private int wanderRange = 10;

        public Wandering(Spider spider){
            this.spider = spider;
        }


        @Override
        public void behave() {

        }

    }
    
    /**
     * Trapping
     */
    private static class Trapping extends Behavior{
        
        protected Spider spider;
        protected PathFinder pathFinder;
        
        public Trapping(Spider spider){
            this.spider = spider;
        }

        @Override
        public void behave() {
        }
    }

    /**
     * Waiting
     */
    private static class Waiting extends Behavior{

        private Spider spider;
        
        public Waiting(Spider spider){
            this.spider = spider;
        }

        @Override
        public void behave() {

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
        protected Spider spider;
        
        public Web(Spider spider){
            this.spider = spider;
            setCharacter('#');
            setfGColor(TileColor.create(200, 200, 200, 255));
            setbGColor(TileColor.transparent());
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
                getSpace().getTerrains().remove(this);
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
    public static class Webbed extends Status implements OverridesAttack, OverridesMovement, Behavable{

        private int turns;

        public Webbed(int turns){
            this.turns = turns;
            setCharacter('░');
            setbGColor(TileColor.transparent());
            setfGColor(TileColor.create(255, 255, 255, 255));
            setDescriptor("Webbed");
        }

        @Override
        public void overrideMovement(Entity entity, Space toSpace) {
            if (entity instanceof PlayerEntity){
                Display.log("You struggle against the webs.");
            } else {
                Display.log("The " + entity.getName() + " struggles against the webs", entity.getSpace());
            }
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
        
    }

    
}
