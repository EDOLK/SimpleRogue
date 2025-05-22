package game.gameobjects.entities;

import static game.Dungeon.getCurrentFloor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.Dungeon;
import game.Path.PathNotFoundException;
import game.display.Display;
import game.floorgeneration.pools.Pool;
import game.gamelogic.Flammable;
import game.gamelogic.HasDodge;
import game.gamelogic.HasDrops;
import game.gamelogic.HasInventory;
import game.gamelogic.OverridesAttack;
import game.gamelogic.OverridesMovement;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.AnimalHunting;
import game.gamelogic.behavior.AnimalWandering;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.behavior.Behavior;
import game.gameobjects.AttackResult;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.items.Corpse;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Seperate;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.ExposedTrap;
import game.gameobjects.terrains.Fire;
import game.gameobjects.terrains.HiddenTrap;

public class Spider extends Animal implements HasDodge, HasInventory, HasDrops{
    
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

    private Optional<? extends Behavior> getHuntingBehavior(Entity target){
        if (Spider.this.getDistanceFromNest(target.getSpace()) <= Spider.this.maxDistance) {
            try {
                ProtoHunting h = new ProtoHunting(target);
                //TODO: Bandaid fix. Fix properly later.
                if (!h.getPath()[h.getPath().length-1].isOccupied() || h.getPath()[h.getPath().length-1].getOccupant() != target) {
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
        return 10;
    }

    protected int getDistanceFromNest(){
        return getDistanceFromNest(this.getSpace());
    }

    protected int getDistanceFromNest(Space space){
        return Math.max(Math.abs(space.getX() - this.nestSpace.getX()), Math.abs(space.getY() - this.nestSpace.getY()));
    }


    /**
     * Nesting
     */
    protected class Nesting extends Behavior{

        protected Nesting(){}

        @Override
        public int behave() {
            Spider.this.nestSpace = Spider.this.getSpace();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    Space pSpace = getCurrentFloor().getSpace(getX()+i, getY()+j);
                    if (!pSpace.isOccupied() || pSpace.getOccupant() == Spider.this){
                        pSpace.addTerrain(new Web());
                    }
                }
            }
            setBehavior(new ProtoTrapping());
            return Spider.this.getTimeToWait();
        }

        @Override
        public boolean isActive() {
            return true;
        }
        
    }

    protected class ProtoTrapping extends AnimalWandering {

        public ProtoTrapping() {
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
                return Optional.of(new ProtoTrapping());
            }
            return Optional.of(new GoingToNest());
        }

        @Override
        public int behave() {
            int time = super.behave();
            if (Spider.this.getBehavior() == this && Spider.this.traps < Spider.this.maxTraps && this.locationInPath == this.path.length-1) {
                Spider.this.getSpace().addTerrain(new Web());
                Spider.this.traps++;
            }
            return time;
        }

        
    }
    
    protected class ProtoHunting extends AnimalHunting{

        public ProtoHunting(Entity target) throws PathNotFoundException {
            super(Spider.this, target);
        }

        @Override
        protected Optional<? extends Behavior> getWanderingBehavior() {
            return Optional.of(new ProtoTrapping());
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
            return Optional.of(new ProtoTrapping());
        }


        @Override
        public int behave() {
            int time = super.behave();
            if (Spider.this.getSpace() == Spider.this.nestSpace && !(Spider.this.getBehavior() instanceof AnimalHunting)) {
                Spider.this.setBehavior(new ProtoWaiting());
                return time;
            }
            return time;
        }

    }

    protected class ProtoWaiting extends Behavior{

        protected ProtoWaiting(){}

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
                    return Spider.this.setAndBehave(new ProtoHunting(random));
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

            if (Spider.this.isEnemy(entity)) {
                t = true;
                entity.addStatus(new Webbed(5));
                getSpace().removeTerrain(this);
                Spider.this.traps--;
                if (!(Spider.this.getBehavior() instanceof AnimalHunting)) {
                    if (Spider.this.getDistanceFromNest(entity.getSpace()) <= Spider.this.maxDistance) {
                        try {
                            Spider.this.setBehavior(new ProtoHunting(entity));
                        } catch (Exception e) {

                        }
                    } else {
                        Spider.this.setBehavior(new ProtoTrapping());
                    }
                }
            }

            //if (!(entity instanceof Spider)){
            //    if (entity instanceof PlayerEntity){
            //        Display.log("You get stuck in the webs!");
            //    } else{
            //        Display.log("The" + entity.getName() + " gets stuck in the webs!", entity.getSpace());
            //    }
            //    t = true;
            //    entity.addStatus(new Webbed(5));
            //    getSpace().removeTerrain(this);
            //    Spider.this.traps--;
            //    if (!(Spider.this.getBehavior() instanceof Hunting)){
            //        if (Spider.this.getDistanceFromNest() < Spider.this.maxDistance){
            //            Spider.this.setBehavior(new Hunting(entity));
            //        }
            //    }
            //} else {
            //    t = false;
            //}
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
        public AttackResult overrideAttack(Entity attacker, Entity attackee, Weapon attackerWeapon) {
            if (attacker instanceof PlayerEntity){
                Display.log("You struggle against the webs.");
            } else {
                Display.log("The " + attacker.getName() + " struggles against the webs", attacker.getSpace());
            }
            return new AttackResult(false, false, 0, null, attacker, attackee);
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

    @Override
    public Pool<Supplier<Item>> getItemPool() {
        return Dungeon.getCurrentDropPool();
    }

    @Override
    public int getDropPoints() {
        return App.randomNumber(5,10);
    }

    
}
