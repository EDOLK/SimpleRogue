package game.gameobjects.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.Attribute;
import game.gamelogic.AttributeMap;
import game.gamelogic.Experiential;
import game.gamelogic.HasAttributes;
import game.gamelogic.HasDodge;
import game.gamelogic.HasInventory;
import game.gamelogic.HasOffHand;
import game.gamelogic.HasSkills;
import game.gamelogic.Levelable;
import game.gamelogic.Skill;
import game.gamelogic.SkillMap;
import game.gamelogic.abilities.Ability;
import game.gamelogic.abilities.HasAbilities;
import game.gamelogic.abilities.HasPassives;
import game.gamelogic.abilities.Passive;
import game.gamelogic.skilltrees.SkillTree;
import game.gamelogic.skilltrees.UsesSkillTrees;
import game.gameobjects.ArmorSlot;
import game.gameobjects.DamageType;
import game.gameobjects.ItemSlot;
import game.gameobjects.Space;
import game.gameobjects.WeaponSlot;
import game.gameobjects.items.Item;
import game.gameobjects.items.Torch;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.armor.ArmorType;
import game.gameobjects.items.weapons.Weapon;

public class PlayerEntity extends Entity implements Armored, Armed, Levelable, Experiential, HasInventory, HasOffHand, HasAbilities, HasDodge, HasAttributes, HasSkills, UsesSkillTrees, HasPassives{

    private int maxWeight = 60;
    private int maxMP;
    private int MP;
    private List<ArmorSlot> armorSlots = new ArrayList<>();
    private List<WeaponSlot> weaponSlots = new ArrayList<>();
    private List<Item> inventory = new ArrayList<>();
    private List<Ability> abilities = new ArrayList<>();
    private List<Passive> passives = new ArrayList<>();
    private ItemSlot offHandSlot = new ItemSlot("Offhand");
    private int level = 1;
    private int XP = 0;
    private int XPToNextLevel = 15;
    private AttributeMap aMap = new AttributeMap();
    private SkillMap sMap = new SkillMap();
    private int attributePoints = 0;
    private int skillTreePoints = 0;
    private Map<SkillTree, Integer> skillLevels = new HashMap<>();

    public PlayerEntity(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
        setBaseMaxHP(15);
        setHP(15);
        setMaxMP(15);
        setMP(15);
        setVisionRange(15);
        setWeight(0);
        setTileName("Player");
        setNightVisionRange(1);

        Weapon fists = new Weapon();
        fists.setDamageType(DamageType.BLUNT);
        fists.setDamage(1,3);
        setUnarmedWeapon(fists);

        Weapon club = new Weapon();
        club.setDamageType(DamageType.BLUNT);
        club.setDamage(1, 4);
        club.setCharacter('c');
        club.setFgColor(TileColor.create(135, 70, 30, 255));
        club.setBgColor(TileColor.transparent());
        club.setName("Wooden Club");
        club.setDescription("A simple wooden club.");
        club.setWeight(5);
        club.setTileName("Club");

        armorSlots.add(new ArmorSlot(ArmorType.HEAD));
        armorSlots.add(new ArmorSlot(ArmorType.CHEST_OUTER));
        armorSlots.add(new ArmorSlot(ArmorType.CHEST_INNER));
        armorSlots.add(new ArmorSlot(ArmorType.LEGS));
        armorSlots.add(new ArmorSlot(ArmorType.HANDS));
        armorSlots.add(new ArmorSlot(ArmorType.FEET));

        WeaponSlot e = new WeaponSlot("Primary Weapon", 1.0);
        weaponSlots.add(e);
        e.setEquippedWeapon(club);
        offHandSlot.setEquippedItem(new Torch(true));

        this.enduranceHPMult = 3;

    }

    @Override
    public boolean addAbility(Ability ability){
        if (abilities.add(ability)){
            Display.logHeader("You gain the " + "\"" + ability.getName() + "\" ability.");
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAbility(Ability ability){
        return abilities.remove(ability);
    }

    @Override
    public List<Ability> getAbilities() {
        return abilities;
    }

    @Override
    public List<Passive> getPassives() {
        return passives;
    }

    @Override
    public boolean addPassive(Passive passive) {
        return passives.add(passive);
    }

    @Override
    public boolean removePassive(Passive passive) {
        return passives.remove(passive);
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
    public String getDeathMessage() {
        return "You are dead!";
    }

    @Override
    public int defaultInteraction(Entity entity) {
        return 100;
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
            attributePoints++;
            skillTreePoints++;
            this.XP -= XPToNextLevel;
            XPToNextLevel = 10 + (level * 5);
            Display.logHeader("Level up!");
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
    public int getHardWeightLimit() {
        return maxWeight + (Attribute.getAttribute(Attribute.STRENGTH, this) * 5);
    }

    @Override
    public List<WeaponSlot> getWeaponSlots() {
        return weaponSlots;
    }

    @Override
    public boolean dropsEquippedWeaponsOnKill() {
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

    @Override
    public ItemSlot getOffHandSlot() {
        return offHandSlot;
    }

    @Override
    public int getDodge() {
        return Attribute.getAttribute(Attribute.DEXTERITY, this) + (Skill.getSkill(Skill.ATHLETICS, this) * 3);
    }

    @Override
    public AttributeMap getAttributeMap() {
        return this.aMap;
    }

    @Override
    public SkillMap getSkillMap() {
        return this.sMap;
    }

    @Override
    public Map<SkillTree, Integer> getSkillLevels() {
        return skillLevels;
    }

    @Override
    public int getAttributePoints() {
        return this.attributePoints;
    }

    @Override
    public void setAttributePoints(int att) {
        this.attributePoints = att;
    }

    @Override
    public int getSkillTreePoints() {
        return this.skillTreePoints;
    }

    @Override
    public void setSkillTreePoints(int points) {
        this.skillTreePoints = points;
    }


}
