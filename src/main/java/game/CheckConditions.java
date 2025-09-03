package game;

public class CheckConditions {

    private boolean statuses;
    private boolean unarmedWeapon;
    private boolean armedWeapons;
    private boolean armors;
    private boolean enchantments;
    private boolean inventory;
    private boolean ability;
    private boolean passive;

    public boolean includesAbility() {
        return ability;
    }

    public boolean includesStatuses() {
        return statuses;
    }

    public boolean includesUnarmedWeapon() {
        return unarmedWeapon;
    }

    public boolean includesArmedWeapons() {
        return armedWeapons;
    }

    public boolean includesArmors() {
        return armors;
    }

    public boolean includesEnchantments() {
        return enchantments;
    }

    public boolean includesInventory(){
        return inventory;
    }

    public boolean includesPassive(){
        return passive;
    }

    public CheckConditions withStatuses(boolean value) {
        this.statuses = value;
        return this;
    }

    public CheckConditions withUnarmedWeapon(boolean value) {
        this.unarmedWeapon = value;
        return this;
    }

    public CheckConditions withArmedWeapons(boolean value) {
        this.armedWeapons = value;
        return this;
    }

    public CheckConditions withArmors(boolean value) {
        this.armors = value;
        return this;
    }

    public CheckConditions withEnchantments(boolean value){
        this.enchantments = value;
        return this;
    }

    public CheckConditions withInventory(boolean value){
        this.inventory = value;
        return this;
    }

    public CheckConditions withAbility(boolean ability) {
        this.ability = ability;
        return this;
    }

    public CheckConditions withPassive(boolean passive) {
        this.passive = passive;
        return this;
    }

    private CheckConditions(){};

    private CheckConditions(boolean all){
        this.statuses = all;
        this.unarmedWeapon = all;
        this.armedWeapons = all;
        this.armors = all;
        this.enchantments = all;
        this.inventory = all;
        this.ability = all;
        this.passive = all;
    }

    public static CheckConditions all(){
        return new CheckConditions(true);
    }

    public static CheckConditions none(){
        return new CheckConditions(false);
    }
}
