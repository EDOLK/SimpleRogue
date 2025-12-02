package game.gameobjects.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.Dungeon;
import game.Path.PathNotFoundException;
import game.display.Display;
import game.floorgeneration.pools.Pool;
import game.gamelogic.DropsXP;
import game.gamelogic.Flammable;
import game.gamelogic.HasDodge;
import game.gamelogic.HasDrops;
import game.gamelogic.HasInventory;
import game.gamelogic.OverridesAttack;
import game.gamelogic.OverridesMovement;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.AnimalHunting;
import game.gamelogic.behavior.AnimalSearching;
import game.gamelogic.behavior.AnimalWandering;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.behavior.Behavior;
import game.gameobjects.AttackResult;
import game.gameobjects.DamageType;
import game.gameobjects.MovementResult;
import game.gameobjects.Space;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.FiltersIn;
import game.gameobjects.statuses.Sleeping;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.ExposedTrap;

public class Spider extends Animal implements HasDodge, HasInventory, HasDrops, DropsXP{
    
    private List<Item> inventory = new ArrayList<Item>();
    protected Space nestSpace;
    protected int maxDistance = 12;
    protected int traps;
    protected int maxTraps = 4;

    public Spider(){
        super(TileColor.transparent(), TileColor.create(125, 125, 125, 255), 'x');
        setBaseMaxHP(5);
        setHP(5);
        setWeight(5);
        setName("Giant spider");
        setTileName("Spider");
        setDescription("A giant spider");
        setBehavior(new Nesting());

        Weapon fangs = new Weapon();
        fangs.setName("fangs");
        fangs.setDamageType(DamageType.PIERCING);
        fangs.setDamage(1, 6);
        setUnarmedWeapon(fangs);

        this.removeStatus(this.getStatusByClass(Sleeping.class));

    }

    private Optional<? extends Behavior> getHuntingBehavior(Entity target){
        if (Spider.this.getDistanceFromNest(target.getSpace()) <= Spider.this.maxDistance) {
            try {
                SpiderHunting h = new SpiderHunting(target);
                // TODO: Bandaid fix. Fix properly later.
                if (!h.pathIsValid()) {
                    return Optional.empty();
                }
                return Optional.of(h);
            } catch (Exception e) {

            }
        }
        return Optional.empty();
    }

    @Override
    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public int getHardWeightLimit() {
        return 50;
    }

    @Override
    public int getDodge() {
        return 7;
    }

    protected int getDistanceFromNest(){
        return getDistanceFromNest(this.getSpace());
    }

    protected int getDistanceFromNest(Space space){
        return Space.manDist(space, this.nestSpace);
    }

    /**
     * Nesting
     */
    protected class Nesting extends Behavior{

        protected Nesting(){}

        @Override
        public int behave() {
            Spider.this.nestSpace = Spider.this.getSpace();
            Spider.this.nestSpace.addTerrain(new Web());
            for (Space space : Space.getAdjacentSpaces(Spider.this.nestSpace)) {
                space.addTerrain(new Web());
            }
            setBehavior(new SpiderTrapping());
            return Spider.this.getTimeToWait();
        }

        @Override
        public boolean isActive() {
            return true;
        }
        
    }

    protected class SpiderTrapping extends AnimalWandering {

        public SpiderTrapping() {
            super(Spider.this);
        }

        @Override
        protected int getWanderCenterX() {
            return Spider.this.nestSpace.getX();
        }

        @Override
        protected int getWanderCenterY() {
            return Spider.this.nestSpace.getY();
        }

        @Override
        protected List<Space> getWanderSpaces(){
            List<Space> potentialSpaces = super.getWanderSpaces();
            Space.getAdjacentSpaces(Spider.this.nestSpace).forEach((s) -> {
                potentialSpaces.remove(s);
            });
            return potentialSpaces;
        }

        @Override
        protected Optional<? extends Behavior> getHuntingBehavior(Entity target) {
            return Spider.this.getHuntingBehavior(target);
        }

        @Override
        protected Optional<? extends Behavior> getWanderingBehavior() {
            if (Spider.this.traps < Spider.this.maxTraps) {
                return Optional.of(new SpiderTrapping());
            }
            return Optional.of(new GoingToNest());
        }

        @Override
        public int behave() {
            int time = super.behave();
            if (Spider.this.getBehavior() == this && Spider.this.traps < Spider.this.maxTraps && this.path != null && this.locationInPath == this.path.length-1) {
                Spider.this.getSpace().addTerrain(new Web());
                Spider.this.traps++;
            }
            return time;
        }

    }
    
    protected class SpiderHunting extends AnimalHunting{

        public SpiderHunting(Entity target) throws PathNotFoundException {
            super(Spider.this, target);
        }

        @Override
        protected Optional<? extends Behavior> getWanderingBehavior() {
            return Optional.of(new SpiderTrapping());
        }

        @Override
        protected Optional<? extends Behavior> getHuntingBehavior(Entity target) {
            return Spider.this.getHuntingBehavior(target);
        }

        @Override
        protected Optional<? extends Behavior> getSearchingBehavior(Space space) {
            return Optional.of(new SpiderSearching(animal, space));
        }

    }

    protected class SpiderSearching extends AnimalSearching{

        public SpiderSearching(Animal animal, Space lastSpace) {
            super(animal, lastSpace);
        }

        @Override
        protected Optional<? extends Behavior> getWanderingBehavior() {
            return Optional.of(new SpiderSearching(animal, lastSpace));
        }

        @Override
        protected Optional<? extends Behavior> getSearchDoneBehavior() {
            return Optional.of(new SpiderTrapping());
        }

        @Override
        protected Optional<? extends Behavior> getHuntingBehavior(Entity target) {
            return Spider.this.getHuntingBehavior(target);
        }


    }

    protected class GoingToNest extends AnimalWandering{

        public GoingToNest() {
            super(Spider.this);
        }

        
        @Override
        protected List<Space> getWanderSpaces(){
            return new ArrayList<Space>(List.of(Spider.this.nestSpace));
        }

        @Override
        protected Optional<? extends Behavior> getHuntingBehavior(Entity target) {
            return Spider.this.getHuntingBehavior(target);
        }

        @Override
        protected Optional<? extends Behavior> getWanderingBehavior() {
            return Optional.of(new SpiderTrapping());
        }

        @Override
        public int behave() {
            int time = super.behave();
            if (Spider.this.getSpace() == Spider.this.nestSpace && !(Spider.this.getBehavior() instanceof AnimalHunting)) {
                Spider.this.setBehavior(new SpiderWaiting());
                return time;
            }
            return time;
        }

    }

    protected class SpiderWaiting extends Behavior{

        protected SpiderWaiting(){}

        @Override
        public int behave() {
            Space curr = Spider.this.getSpace();
            List<Entity> enemies = new ArrayList<>();
            for (Space space : Space.getAdjacentSpaces(curr)) {
                if (space.isOccupied() && Spider.this.isEnemy(space.getOccupant())) {
                    enemies.add(space.getOccupant());
                }
            }
            while (!enemies.isEmpty()) {
                Entity random = App.getRandom(enemies);
                try {
                    return Spider.this.setAndBehave(new SpiderHunting(random));
                } catch (Exception e) {
                    enemies.remove(random);
                }
            }
            return Spider.this.getTimeToWait();
        }

        @Override
        public boolean isActive() {
            return Spider.this.isAlive();
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
            setFgColor(TileColor.create(200, 200, 200, 255));
            setBgColor(TileColor.transparent());
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
            if (entity != Spider.this) {
                t = true;
                entity.addStatus(new Webbed(5));
                getSpace().removeTerrain(this);
                Spider.this.traps--;
                if (!(Spider.this.getBehavior() instanceof AnimalHunting) && Spider.this.isEnemy(entity)){
                    Optional<? extends Behavior> hunt = Spider.this.getHuntingBehavior(entity);
                    if (hunt.isPresent()) {
                        Spider.this.setBehavior(hunt.get());
                    }
                } else {
                    Spider.this.setBehavior(new SpiderTrapping());
                }
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
        public void onBurn() {
            getSpace().remove(this);
        }
        
    }
    
    /**
     * Webbed
     */
    public static class Webbed extends Status implements OverridesAttack, OverridesMovement, Behavable, FiltersIn{

        private int turns;

        public Webbed(int turns){
            this.turns = turns;
            setCharacter('░');
            setBgColor(TileColor.transparent());
            setFgColor(TileColor.create(255, 255, 255, 255));
            setDescriptor("Webbed");
            setTileName("Web");
        }

        @Override
        public MovementResult overrideMovement(MovementResult result, Entity entity, Space toSpace) {
            if (entity instanceof PlayerEntity){
                Display.log("You struggle against the webs.");
            } else {
                Display.log("The " + entity.getName() + " struggles against the webs", entity.getSpace());
            }
            return result.withSuccessful(false);
        }

        @Override
        public AttackResult overrideAttack(Entity attacker, Entity attackee, Weapon attackerWeapon) {
            if (attacker instanceof PlayerEntity){
                Display.log("You struggle against the webs.");
            } else {
                Display.log("The " + attacker.getName() + " struggles against the webs", attacker.getSpace());
            }
            return new AttackResult(attacker, attackee);
        }

        @Override
        public List<AttackResult> overrideAttack(Entity attacker, Entity attackee) {
            return List.of(overrideAttack(attacker,attackee,null));
        }

        @Override
        public int behave() {
            turns--;
            if (turns <= 0){
                owner.removeStatus(this);
            }
            return 100;
        }

        @Override
        public boolean isActive() {
            return this.getOwner() != null;
        }

        @Override
        public boolean filterIn(Status status) {
            if (status instanceof Webbed webbed) {
                webbed.turns += this.turns;
                return true;
            }
            return false;
        }

        @Override
        public void onStatusAdd() {
            if (this.owner instanceof PlayerEntity) {
                Display.logHeader("You are webbed!");
            }
        }

    }

    @Override
    public Pool<Item> getItemPool() {
        return Dungeon.getCurrentDropPool();
    }

    @Override
    public int getDropPoints() {
        return App.randomNumber(5,10);
    }

    @Override
    public int dropXP() {
        return 10;
    }

    
}
