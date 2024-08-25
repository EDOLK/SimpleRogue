package game.gameobjects.entities;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.display.FloorMenu.State;
import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.Experiential;
import game.gamelogic.HasInventory;
import game.gamelogic.Levelable;
import game.gamelogic.LightSource;
import game.gamelogic.SelfAware;
import game.gameobjects.ArmorSlot;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.WeaponSlot;
import game.gameobjects.items.Corpse;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.armor.ArmorType;
import game.gameobjects.items.weapons.Weapon;

public class PlayerEntity extends Entity implements Armored, Armed, Levelable, Experiential, HasInventory, LightSource{

    private int maxWeight = 50;
    private int maxMP;
    private int MP;
    private List<ArmorSlot> armorSlots = new ArrayList<ArmorSlot>();
    private List<WeaponSlot> weaponSlots = new ArrayList<WeaponSlot>();
    private List<Item> inventory = new ArrayList<Item>();
    private int level = 1;
    private int XP = 0;
    private int XPToNextLevel = 15;

    public PlayerEntity(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
        setMaxHP(15);
        setHP(15);
        setMaxMP(15);
        setMP(15);
        setVisionRange(15);
        setWeight(0);
        setCorpse(new Corpse(this));
        setTileName("Player");
        Weapon fists = new Weapon();
        setUnarmedWeapon(fists);

        Weapon club = new Weapon();
        club.setDamageType(DamageType.BLUNT);
        club.setDamage(1, 4);
        club.setCharacter('c');
        club.setfGColor(TileColor.create(135, 70, 30, 255));
        club.setbGColor(TileColor.transparent());
        club.setName("Wooden Club");
        club.setDescription("A simple wooden club.");
        club.setWeight(5);
        club.setTileName("Club");
        addItemToInventory(club);
        
        armorSlots.add(new ArmorSlot(ArmorType.HEAD));
        armorSlots.add(new ArmorSlot(ArmorType.CHEST_OUTER));
        armorSlots.add(new ArmorSlot(ArmorType.CHEST_INNER));
        armorSlots.add(new ArmorSlot(ArmorType.LEGS));
        armorSlots.add(new ArmorSlot(ArmorType.HANDS));
        armorSlots.add(new ArmorSlot(ArmorType.FEET));
        
        weaponSlots.add(new WeaponSlot("Primary Weapon", 1.0));
        
    }

    public int getMaxMP() {
        return maxMP;
    }

    public void setMaxMP(int maxMP) {
        this.maxMP = maxMP;
    }

    public int getMP() {
        return MP;
    }

    public void setMP(int mP) {
        MP = mP;
    }

    public void setArmorBySlot(ArmorSlot slot, Armor newArmor){
        try {
            addItemToInventory(slot.setEquippedArmor(newArmor));
        } catch (Exception e){
            Display.log(e.getMessage());
        }
    }

    public void dropItem(Item item, Space space){
        if (inventory.contains(item)){
            removeItemFromInventory(item);
            space.addItem(item);
        }
    }

    @Override
    public void onKill(Entity killer) {
        Space currentSpace = getSpace();
        currentSpace.setOccupant(null);
        currentSpace.addItem(getCorpse());
        if (getCorpse() instanceof SelfAware selfAware){
            selfAware.setSpace(currentSpace);
        }
        Display.log("You are dead!");
        Display.getFloorMenu().setCurrentState(State.DEAD);
    }

    @Override
    public void defaultInteraction(Entity entity) {

    }

    @Override
    public List<ArmorSlot> getArmorSlots() {
        return armorSlots;
    }

    @Override
    public int getXP() {
        return XP;
    }

    @Override
    public void addXP(int XP) {
        this.XP += XP;
        while (this.XP >= XPToNextLevel) {
            level++;
            this.XP -= XPToNextLevel;
            XPToNextLevel = 10 + (level * 5);
            setMaxHP(getMaxHP() + 5);
            setHP(getHP() + 5);
            setMaxMP(getMaxMP() + 5);
            setMP(getMP() + 5);
            Display.log("Level up!");
        }
    }

    @Override
    public int getXPToNextLevel() {
        return XPToNextLevel;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public int getMaxWeight() {
        return maxWeight;
    }

    @Override
    public int getLightSourceIntensity() {
        return 7;
    }

    @Override
    public List<WeaponSlot> getWeaponSlots() {
        return weaponSlots;
    }

    @Override
    public boolean dropsEquipedWeaponsOnKill() {
        return false;
    }

    @Override
    public boolean dropsEquipedArmorsOnKill() {
        return false;
    }

    @Override
    public boolean setLevel(int level) {
        this.level = level;
        return true;
    }

}
