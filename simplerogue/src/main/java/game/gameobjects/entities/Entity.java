package game.gameobjects.entities;

import static game.App.lerp;
import static game.Dungeon.getCurrentFloor;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.Line;
import game.display.Display;
import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.DropsXP;
import game.gamelogic.Examinable;
import game.gamelogic.Experiential;
import game.gamelogic.HasDrops;
import game.gamelogic.HasInventory;
import game.gamelogic.HasResistances;
import game.gamelogic.HasVulnerabilities;
import game.gamelogic.SelfAware;
import game.gamelogic.resistances.Resistance;
import game.gamelogic.time.ModifiesMoveTime;
import game.gamelogic.time.ModifiesAttackTime;
import game.gameobjects.DamageType;
import game.gameobjects.DisplayableTile;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.items.Corpse;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.SeperateIn;
import game.gameobjects.statuses.SeperateOut;
import game.gameobjects.statuses.Status;

public abstract class Entity extends DisplayableTile implements Examinable, SelfAware{

    private String name = "Placeholder Name";
    private String description = "Placeholder description.";
    protected Space currentSpace;
    private int maxHP;
    private int HP;
    private int visionRange = 10;
    private int nightVisionRange = 1;
    private int weight;
    private ArrayList<Status> statuses = new ArrayList<Status>();
    protected boolean sightBlocker = false;
    protected boolean gasBlocker = false;
    protected boolean liquidBlocker = false;
    protected boolean lightBlocker = false;
    protected boolean itemBlocker = false;
    private Weapon unarmedWeapon;
    private boolean alive = true;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Weapon getUnarmedWeapon() {
        return unarmedWeapon;
    }

    public void setUnarmedWeapon(Weapon unarmedWeapon) {
        this.unarmedWeapon = unarmedWeapon;
    }

    public boolean isLiquidBlocker() {
        return liquidBlocker;
    }

    public void setLiquidBlocker(boolean liquidBlocker) {
        this.liquidBlocker = liquidBlocker;
    }

    public boolean isLightBlocker() {
        return lightBlocker;
    }

    public void setLightBlocker(boolean lightBlocker) {
        this.lightBlocker = lightBlocker;
    }

    public Entity(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
    }

    public Entity(){
        super();
    }

    public boolean isGasBlocker() {
        return gasBlocker;
    }

    public void setGasBlocker(boolean gasBlocker) {
        this.gasBlocker = gasBlocker;
    }
    
    public int getWeight() {
        int w = weight;
        if (this instanceof HasInventory hasInventory){
            w += hasInventory.getInventoryWeight();
        }
        return w;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isSightBlocker() {
        return sightBlocker;
    }

    public void setSightBlocker(boolean blockSight) {
        this.sightBlocker = blockSight; 
    }

    public int getVisionRange() {
        return visionRange;
    }

    public void setVisionRange(int visionRange) {
        this.visionRange = visionRange;
    }

    public String getOriginalName(){
        return this.name;
    }

    public String getName() {
        String n = this.name;
        for (Status status : statuses) {
            if (status.getDescriptor() != null && !status.getDescriptor().equals("")){
                n = status.getDescriptor() + " " + n;
            }
        }
        return n;
    }
    
    public String getTrueName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Item getCorpse() {
        return new Corpse(this);
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHealth) {
        this.maxHP = maxHealth;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int health) {
        this.HP = health;
    }
    
    public int getBaseWeight(){
        return weight;
    }

    @Override
    public Space getSpace(){
        return currentSpace;
    }

    @Override
    public void setSpace(Space newSpace){
        currentSpace = newSpace;
    }

    protected boolean isVulnerable(Class<? extends Status> status){
        return true;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }
    
    public boolean addStatus(Status status){
        if (status == null){
            return false;
        }
        if (!isVulnerable(status.getClass())){
            return false;
        }
        
        List<Status> filteredIn = new ArrayList<>();
        if (status instanceof SeperateIn seperate){
            for (Status st : statuses) {
                if (seperate.validateSamenessIn(st)) {
                    filteredIn.add(st);
                }
            }
        }

        List<SeperateOut> filteredOut = new ArrayList<>();
        for (Status status2 : statuses) {
            if (status2 instanceof SeperateOut seperate) {
                if (seperate.validateSamenessOut(status)) {
                    filteredOut.add(seperate);
                }
            }
        }

        for (Status sIn : filteredIn) {
            ((SeperateIn)status).onStackIn(sIn);
        }

        for (SeperateOut sOut : filteredOut) {
            sOut.onStackOut(status);
        }

        if (!filteredIn.isEmpty() || !filteredOut.isEmpty()) {
            return false;
        }

        if (getStatuses().add(status)){
            status.setOwner(this);
            return true;
        }

        return false;
    }

    public boolean removeStatus(Status status){
        if (status != null){
            if (getStatuses().remove(status)){
                status.setOwner(null);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    // public boolean hasStatus(Class<?> statusClass){
    //     for (Status status : statuses) {
    //         if (statusClass.isInstance(status)){
    //             return true;
    //         }
    //     }
    //     return false;
    // }
    
    public Status getStatusByClass(Class<?> statusClass){
        for (Status status : statuses) {
            if (statusClass.isInstance(status)){
                return status;
            }
        }
        return null;
    }

    public List<Entity> getEntitiesInVision(){
        List<Entity> list = new ArrayList<>();
        for (Space space : this.getSpacesInVision()) {
            if (space.isOccupied()) {
                list.add(space.getOccupant());
            }
        }
        return list;
    }

    public List<Space> getSpacesInVision(){
        return getSpacesInVision(false);
    }

    public List<Space> getSpacesInVision(boolean seeSelf){

        List<Space> spacesInVision = new ArrayList<Space>();

        int visRange = getVisionRange() > getNightVisionRange() ? getVisionRange() : getNightVisionRange();

        int startX = getCurrentFloor().clampX(getX() - visRange);
        int endX = getCurrentFloor().clampX(getX() + visRange);
        int startY = getCurrentFloor().clampY(getY() - visRange);
        int endY = getCurrentFloor().clampY(getY() + visRange);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                Space potentialSpace = getCurrentFloor().getSpace(x,y);
                if (isWithinVision(potentialSpace)){
                    if (!seeSelf && potentialSpace == getSpace()) {
                        continue;
                    }
                    spacesInVision.add(potentialSpace);
                }
            }
        }

        return spacesInVision;
    }

    private boolean isBeyondNightVision(Space potentialSpace) {
        return Space.getDistance(getSpace(), potentialSpace) > getNightVisionRange() && potentialSpace.getLight() <= 0;
    }

    public int getNightVisionRange() {
        return this.nightVisionRange;
    }

    public void setNightVisionRange(int nightVisionRange) {
        this.nightVisionRange = nightVisionRange;
    }

    public boolean isWithinVision(Entity entity){
        return isWithinVision(entity.getSpace());
    }

    public boolean isWithinVision(Space space){
        if (canDrawLine(space) && !isBeyondNightVision(space)) {
            return true;
        } else {
            for (Space p : Space.getAdjacentSpaces(space)) {
                if (canDrawLine(p) && !isBeyondNightVision(space) && (!p.isOccupied() || !p.getOccupant().isSightBlocker())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canDrawLine(Space space) {
        List<Space> lineSpaces = Line.getLineAsListExclusive(currentSpace,space);
        for (Space currentSpace : lineSpaces) {
            if (currentSpace.isOccupied()){
                Entity occupant = currentSpace.getOccupant();
                if (occupant.isSightBlocker()){
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isAdjacent(SelfAware selfAware){
        return isAdjacent(selfAware.getSpace());
    }

    public boolean isAdjacent(Space space){
        return Space.getAdjacentSpaces(this.getSpace()).contains(space);
    }
    
    public void heal(int hp){

        if (hp <= 0){
            return;
        }

        if (HP + hp >= maxHP){
            HP = maxHP;
        } else {
            HP += hp;
        }
        
        if (this instanceof PlayerEntity){
            Display.log("You heal " + hp + " HP.");
        } else {
            Display.log("The " + getName() + " heals " + hp + " HP.", getSpace());
        }

    }

    public int dealDamage(int damage, DamageType damageType, Entity attacker){

        if (attacker == null){
            return dealDamage(damage, damageType);
        }

        damage = doResistances(damage, damageType);
        damage = doVulnerabilities(damage, damageType);

        if (attacker instanceof PlayerEntity){
            Display.log("You attack the " + getName() + " for " + damage + " " + damageType + " damage.");
        } else if (this instanceof PlayerEntity){
            Display.log(attacker.getName() + " attacks you for " + damage + " " + damageType + " damage.");
        } else {
            Display.log(attacker.getName() + " attacks the " + getName() + " for " + damage + " " + damageType + " damage.", getSpace());
        }

        HP -= damage;

        if (getHP() <= 0){
            setHP(0);
            kill(attacker);
        }
        return damage;
    }

    public int dealDamage(int damage, DamageType damageType){

        damage = doResistances(damage, damageType);
        damage = doVulnerabilities(damage, damageType);

        if (damage > 0) {
            if (this instanceof PlayerEntity){
                Display.log("You take " + damage + " " + damageType + " damage.");
            } else {
                Display.log(getName() + " takes " + damage + " " + damageType + " damage.", getSpace());
            }
        }


        HP -= damage;

        if (getHP() <= 0){
            setHP(0);
            kill(null);
        }
        return damage;
    }
    
    public int doResistances(int damage, DamageType damageType){

        List<HasResistances> hasResistancesList = collectHasResistances();

        for (HasResistances hasResistances : hasResistancesList) {
            damage = hasResistances.applyResistances(damage, damageType);
        }

        return damage;

    }

    public List<Resistance> collectResistances(){
        List<HasResistances> hasResistancesList = collectHasResistances();
        List<Resistance> resistances = new ArrayList<>();
        for (HasResistances hasResistances : hasResistancesList) {
            resistances.addAll(hasResistances.getResistances());
        }
        return resistances;
    }

    public List<HasResistances> collectHasResistances(){

        List<HasResistances> resistances = new ArrayList<>();

        if (this instanceof HasResistances hasResistances){
            resistances.add(hasResistances);
        }

        if (this instanceof Armored armored){
            for (Armor armor : armored.getArmor()) {
                resistances.add(armor);
            }
        }
        
        for (Status status : getStatuses()) {
            if (status instanceof HasResistances statusWithResistances){
                resistances.add(statusWithResistances);
            }
        }
        return resistances;
    }

    public int doVulnerabilities(int damage, DamageType damageType){

        if (this instanceof HasVulnerabilities hasVulnerabilities){
            damage = hasVulnerabilities.applyVulnerabilities(damage, damageType);
        }

        for (Status status : getStatuses()) {
            if (status instanceof HasVulnerabilities statusWithVulnerabilities){
                damage = statusWithVulnerabilities.applyVulnerabilities(damage, damageType);
            }
        }

        return damage;
    }

    // on death behavior (most will just drop a corpse, and other drops)
    public void kill(Entity killer){

        if (getDeathMessage() != null && !getDeathMessage().equals("")) {
            Display.log(getDeathMessage(), getSpace());
        }

        Space space = getSpace();

        space.setOccupant(null);

        if (this instanceof HasInventory hasInventory){
            for (Item item : hasInventory.getInventory()) {
                space.addItem(item);
            }
        }

        if (this instanceof HasDrops hasDrops){
            for (Item item : hasDrops.generateDrops()){
                space.addItem(item);
            }
        }

        if (this instanceof Armored armored && armored.dropsEquipedArmorsOnKill()){
            for (Armor armor : armored.getArmor()) {
                space.addItem(armor);
            }
        }

        if (this instanceof Armed armed && armed.dropsEquippedWeaponsOnKill()){
            for (Weapon weapon : armed.getWeapons()) {
                space.addItem(weapon);
            }
        }

        if (killer instanceof Experiential eEntity && this instanceof DropsXP dropsXP){
            eEntity.addXP(dropsXP.dropXP());
        }

        space.addItem(getCorpse());

        setAlive(false);

    }

    public String getDeathMessage(){
        return null;
    }

    // what this entity will do by default if another walks into it (should reference one of the other behavioral functions, perhaps after determining what the other entity is)
    public int defaultInteraction(Entity interactor){
        Floor.doAttack(interactor, this);
        return interactor.getTimeToAttack();
    };

    public int getBaseMoveTime(){
        return 100;
    }

    public int getTimeToMove(){
        int t = getBaseMoveTime();
        if (this instanceof HasInventory hasInventory) {
            int diff = hasInventory.getInventoryWeight() - hasInventory.getSoftWeightLimit();
            if (diff > 0) {
                t += lerp(
                    0,
                    0,
                    hasInventory.getHardWeightLimit()-hasInventory.getSoftWeightLimit(),
                    getBaseMoveTime(),
                    diff
                );
            }
        }
        for (Status status : statuses) {
            if (status instanceof ModifiesMoveTime mmt){
                t = mmt.modifyTime(t);
            }
        }
        return t;
    }

    public int getBaseAttackTime(){
        return 100;
    }

    public int getTimeToAttack(){
        int t = getBaseAttackTime();
        for (Status status : statuses) {
            if (status instanceof ModifiesAttackTime mat){
                t = mat.modifyTime(t);
            }
        }
        return t;
    }

    public int getBaseWaitTime(){
        return 100;
    }

    public int getTimeToWait(){
        return getBaseWaitTime();
    }

}
