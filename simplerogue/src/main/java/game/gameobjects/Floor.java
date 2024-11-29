package game.gameobjects;
import static game.App.lerp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.hexworks.zircon.api.color.TileColor;

import game.App;
import game.Line;
import game.display.Display;
import game.floorgeneration.FloorGenerator;
import game.gamelogic.Armed;
import game.gamelogic.Armored;
import game.gamelogic.HasAccuracy;
import game.gamelogic.HasDodge;
import game.gamelogic.LightSource;
import game.gamelogic.OverridesAttack;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.CombatModifier;
import game.gamelogic.combat.OnAttack;
import game.gamelogic.combat.OnAttacked;
import game.gamelogic.combat.OnCrit;
import game.gamelogic.combat.OnCritted;
import game.gamelogic.combat.OnHit;
import game.gamelogic.combat.OnHitted;
import game.gamelogic.combat.OnMiss;
import game.gamelogic.combat.OnMissed;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.items.Item;
import game.gameobjects.items.armor.Armor;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Status;
import game.gameobjects.terrains.Terrain;

public class Floor{

	public final int SIZE_X;
	public final int SIZE_Y;

	private Space[][] spaces;
	private PlayerEntity player;

	public Floor(int SIZE_X, int SIZE_Y, FloorGenerator floorGenerator){
		this(SIZE_X, SIZE_Y, new PlayerEntity(TileColor.transparent(), TileColor.create(255, 255, 255, 255), '@'), floorGenerator);
	}

	public Floor(int SIZE_X, int SIZE_Y, PlayerEntity player, FloorGenerator floorGenerator){

		this.SIZE_X = SIZE_X;
		this.SIZE_Y = SIZE_Y;
		spaces = new Space[SIZE_X][SIZE_Y];
		this.player = player;
		floorGenerator.generateFloor(spaces, player);

		for (int x = 0; x < spaces.length; x++) {
			for (int y = 0; y < spaces[x].length; y++) {
				spaces[x][y].setLight(0.0);
			}
		}
		for (int x = 0; x < spaces.length; x++) {
			for (int y = 0; y < spaces[x].length; y++) {
                doLight(spaces[x][y]);
			}
		}

	}

	public PlayerEntity getPlayer() {
		return player;
	}

	public Space[][] getSpaces() {
		return spaces;
	}

	public Space getSpace(int x, int y){
		return spaces[x][y];
	}

	public void update(){

		Stack<Behavable> behavables = new Stack<Behavable>();

		for (int x = 0; x < spaces.length; x++) {
			for (int y = 0; y < spaces[x].length; y++) {
				Space currentSpace = getSpace(x, y);
                
                currentSpace.setLight(0.0);

				if (currentSpace.isOccupied()){
					Entity entity = currentSpace.getOccupant();

					if (entity instanceof Behavable behavableEntity){
						behavables.add(behavableEntity);
					}
					
					for (Status status : entity.getStatuses()) {
						if (status instanceof Behavable behavableStatus){
							behavables.add(behavableStatus);
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
			if (behavable.isActive()){
				behavable.behave();
			}
		}

		for (int x = 0; x < spaces.length; x++) {
			for (int y = 0; y < spaces[x].length; y++) {
				doLight(spaces[x][y]);
			}
		}

	}
	
	public void doLight(Space space){
		LightSource strongestLightSource = null;
		int intensity = 0;
		for (Item item : space.getItems()) {
			strongestLightSource = calculateLightSource(strongestLightSource, item);
		}
		for (Terrain terrain : space.getTerrains()) {
            strongestLightSource = calculateLightSource(strongestLightSource, terrain);
		}
		if (space.isOccupied()){
			Entity occupant = space.getOccupant();
            strongestLightSource = calculateLightSource(strongestLightSource, occupant);
			for (Status status : occupant.getStatuses()) {
                strongestLightSource = calculateLightSource(strongestLightSource, status);
			}
		}
        intensity = strongestLightSource != null ? strongestLightSource.getLightSourceIntensity() : 0;
        for (int xDiff = -intensity; xDiff <= intensity; xDiff++) {
            yDiffLoop:
            for (int yDiff = -intensity; yDiff <= intensity; yDiff++) {
                Space querySpace = null;
                try {
                    querySpace = spaces[space.getX() + xDiff][space.getY() + yDiff];
                    List<Space> list = Line.getLineAsArrayList(space,querySpace);
                    for (Space s : list) {
                        if (s.isOccupied() && s.getOccupant().isLightBlocker()){
                            continue yDiffLoop;
                        }
                    }
                    int distance = Math.max(Math.abs(xDiff), Math.abs(yDiff));
                    double light = Math.max(intensity - distance, 0.0);
                    light = light >= 10 ? 1 : lerp(0,0,10,1,light);
                    if (querySpace.getLight() < light){
                        querySpace.setLight(light);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
	}

    private LightSource calculateLightSource(LightSource strongestLightSource, Object object) {
        if (object instanceof LightSource lightSource){
        	if (strongestLightSource == null){
        		strongestLightSource = lightSource;
        	} else if (strongestLightSource.getLightSourceIntensity() < lightSource.getLightSourceIntensity()){
        		strongestLightSource = lightSource;
        	}
        }
        return strongestLightSource;
    }

	public static void doAttack(Entity attacker, Entity defender){

        OverridesAttack overridesAttack = (OverridesAttack)attacker.getStatusByClass(OverridesAttack.class);
        if (overridesAttack != null){
            overridesAttack.overrideAttack(attacker, defender);
            return;
        }

        List<Weapon> attackerActiveWeapons = new ArrayList<Weapon>();
        
        if (attacker instanceof Armed armedAttacker){
            for (WeaponSlot weaponSlot : armedAttacker.getWeaponSlots()) {
                if (weaponSlot.getEquippedWeapon() != null && Math.random() < weaponSlot.getChance()){
                    attackerActiveWeapons.add(weaponSlot.getEquippedWeapon());
                }
            }
        }

        if (attackerActiveWeapons.isEmpty()){
            if (attacker.getUnarmedWeapon() == null){
                if (attacker instanceof PlayerEntity){
                    Display.log("You have no weapon!");
                } else if (defender instanceof PlayerEntity){
                    Display.log("The " + attacker.getName() + " tries to attack you.");
                } else {
                    Display.log(attacker.getName() + " tries to attack the " + defender.getName() + ".", defender.getSpace());
                }
                return;
            } else {
                attackerActiveWeapons.add(attacker.getUnarmedWeapon());
            }
        }

        for (Weapon weapon : attackerActiveWeapons) {
            AttackInfo attackInfo = new AttackInfo(attacker, defender, weapon);
            int defenderDodge = 0;
            
            List<CombatModifier> attackerCombatMods = getCombatModifiers(attacker);
            List<OnAttack> attackerOnAttack = new ArrayList<OnAttack>();
            List<OnAttacked> attackerOnAttacked = new ArrayList<OnAttacked>();
            List<OnHit> attackerOnHit = new ArrayList<OnHit>();
            List<OnHitted> attackerOnHitted = new ArrayList<OnHitted>();
            List<OnMiss> attackerOnMiss = new ArrayList<OnMiss>();
            List<OnMissed> attackerOnMissed = new ArrayList<OnMissed>();
            List<OnCrit> attackerOnCrit = new ArrayList<OnCrit>();
            List<OnCritted> attackerOnCritted = new ArrayList<OnCritted>();
            for (CombatModifier combatModifier : attackerCombatMods) {
                if (combatModifier instanceof OnAttack onAttack){
                    attackerOnAttack.add(onAttack);
                }
                if (combatModifier instanceof OnAttacked onAttacked){
                    attackerOnAttacked.add(onAttacked);
                }
                if (combatModifier instanceof OnHit onHit){
                    attackerOnHit.add(onHit);
                }
                if (combatModifier instanceof OnHitted onHitted){
                    attackerOnHitted.add(onHitted);
                }
                if (combatModifier instanceof OnMiss onMiss){
                    attackerOnMiss.add(onMiss);
                }
                if (combatModifier instanceof OnMissed onMissed){
                    attackerOnMissed.add(onMissed);
                }
                if (combatModifier instanceof OnCrit onCrit){
                    attackerOnCrit.add(onCrit);
                }
                if (combatModifier instanceof OnCritted onCritted){
                    attackerOnCritted.add(onCritted);
                }
            }

            List<CombatModifier> defenderCombatMods = getCombatModifiers(defender);
            List<OnAttack> defenderOnAttack = new ArrayList<OnAttack>();
            List<OnAttacked> defenderOnAttacked = new ArrayList<OnAttacked>();
            List<OnHit> defenderOnHit = new ArrayList<OnHit>();
            List<OnHitted> defenderOnHitted = new ArrayList<OnHitted>();
            List<OnMiss> defenderOnMiss = new ArrayList<OnMiss>();
            List<OnMissed> defenderOnMissed = new ArrayList<OnMissed>();
            List<OnCrit> defenderOnCrit = new ArrayList<OnCrit>();
            List<OnCritted> defenderOnCritted = new ArrayList<OnCritted>();
            for (CombatModifier combatModifier : defenderCombatMods) {
                if (combatModifier instanceof OnAttack onAttack){
                    defenderOnAttack.add(onAttack);
                }
                if (combatModifier instanceof OnAttacked onAttacked){
                    defenderOnAttacked.add(onAttacked);
                }
                if (combatModifier instanceof OnHit onHit){
                    defenderOnHit.add(onHit);
                }
                if (combatModifier instanceof OnHitted onHitted){
                    defenderOnHitted.add(onHitted);
                }
                if (combatModifier instanceof OnMiss onMiss){
                    defenderOnMiss.add(onMiss);
                }
                if (combatModifier instanceof OnMissed onMissed){
                    defenderOnMissed.add(onMissed);
                }
                if (combatModifier instanceof OnCrit onCrit){
                    defenderOnCrit.add(onCrit);
                }
                if (combatModifier instanceof OnCritted onCritted){
                    defenderOnCritted.add(onCritted);
                }
            }

            defenderDodge += getDodge(defender);

            attackInfo.setDefenderDogdge(defenderDodge);

            int naturalAttackerRoll = App.randomNumber(1, 20);

            attackInfo.setBaseRoll(naturalAttackerRoll);

            boolean crit = naturalAttackerRoll == 20;

            attackInfo.setCrit(crit);

            int modifiedAttackerRoll = naturalAttackerRoll;

            modifiedAttackerRoll += getAccuracy(attacker, weapon);

            attackInfo.setModifiedRoll(modifiedAttackerRoll);
            
            int damage = weapon.generateDamage();
            
            damage = crit ? damage * 2 : damage;

            attackInfo.setDamage(damage);

            DamageType attackerDamageType = weapon.getDamageType();
            
            attackInfo.setDamageType(attackerDamageType);

            boolean hit = modifiedAttackerRoll >= defenderDodge;

            attackInfo.setHit(hit);
            
            int damageDelt = 0;

            attackInfo.setDamageDelt(damageDelt);

            // attacker on attack and defender on attacked
            for (OnAttack onAttack : attackerOnAttack) {
                onAttack.activate(attacker, defender, attackInfo);
            }
            for (OnAttacked onAttacked : defenderOnAttacked) {
                onAttacked.activate(defender, attacker, attackInfo);
            }
            //
            
            if (hit){

                damageDelt = defender.dealDamage(damage, attackerDamageType, attacker);

                attackInfo.setDamageDelt(damageDelt);
                // attacker on hit and defender on hitted
                for (OnHit onHit : attackerOnHit) {
                    onHit.activate(attacker, defender, attackInfo);
                }
                for (OnHitted onHitted : defenderOnHitted) {
                    onHitted.activate(defender, attacker, attackInfo);
                }
                //
                
                if (crit){
                    if (attacker instanceof PlayerEntity){
                        Display.log("Critical hit!");
                    } else if (defender instanceof PlayerEntity){
                        Display.log("The " + attacker.getName() + " scores a critical hit on you.");
                    } else {
                        Display.log("The " + attacker.getName() + " scores a critical hit on the " + defender.getName() + ".", attacker.getSpace());
                    }

                    // attacker on crit and defender on critted
                    for (OnCrit onCrit : attackerOnCrit) {
                        onCrit.activate(attacker, defender, attackInfo);
                    }
                    for (OnCritted onCritted : defenderOnCritted) {
                        onCritted.activate(defender, attacker, attackInfo);
                    }

                }
            } else {
                if (attacker instanceof PlayerEntity){
                    Display.log("You miss the " + defender.getName() + ".");
                } else if (defender instanceof PlayerEntity){
                    Display.log("The " + attacker.getName() + " misses you.");
                } else {
                    Display.log(attacker.getName() + " misses the " + defender.getName() + ".", defender.getSpace());
                }

                // attacker on miss and defender on missed
                for (OnMiss onMiss : attackerOnMiss) {
                    onMiss.activate(attacker, defender, attackInfo);
                }
                for (OnMissed onMissed : defenderOnMissed) {
                    onMissed.activate(defender, attacker, attackInfo);
                }
                //
                return;
            }
        }
	}
    
    public static List<CombatModifier> getCombatModifiers(Entity entity){
        List<CombatModifier> combatModifiers = new ArrayList<CombatModifier>();
        if (entity instanceof CombatModifier combatModifierEntity){
            combatModifiers.add(combatModifierEntity);
        }
        if (entity instanceof Armed armedEntity){
            for (Weapon weapon : armedEntity.getWeapons()) {
                if (weapon instanceof CombatModifier combatModifierWeapon){
                    combatModifiers.add(combatModifierWeapon);
                }
                if (weapon.getEnchantment() instanceof CombatModifier combatModifierWeaponEnchantment){
                    combatModifiers.add(combatModifierWeaponEnchantment);
                }
            }
        }
        if (entity instanceof Armored armoredEntity){
            for (Armor armor : armoredEntity.getArmor()) {
                if (armor instanceof CombatModifier combatModifierArmor){
                    combatModifiers.add(combatModifierArmor);
                }
                if (armor.getEnchantment() instanceof CombatModifier combatModifierArmorEnchantment){
                    combatModifiers.add(combatModifierArmorEnchantment);
                }
            }
        }
        for (Status status : entity.getStatuses()) {
            if (status instanceof CombatModifier combatModifierStatus){
                combatModifiers.add(combatModifierStatus);
            }
        }
        return combatModifiers;
    }
    
    public static int getDodge(Entity entity){
        int dodge = 0;
        if (entity instanceof HasDodge hasDodge){
            dodge += hasDodge.getDodge();
        }
        if (entity instanceof Armored armoredEntity){
            for (Armor armor : armoredEntity.getArmor()) {
                dodge += armor.getDodge();
                if (armor.getEnchantment() instanceof HasDodge hasDodgeEnchantment){
                    dodge += hasDodgeEnchantment.getDodge();
                }
            }
        }
        for (Status status : entity.getStatuses()) {
            if (status instanceof HasDodge dodgeStatus){
                dodge += dodgeStatus.getDodge();
            }
        }
        return dodge;
    }
    
    public static int getAccuracy(Entity entity, Weapon activeWeapon){

        int accuracy = 0;
        accuracy += activeWeapon.getAccuracy();

        if (entity instanceof HasAccuracy entityWithAccuracy){
            accuracy += entityWithAccuracy.getAccuracy();
        }
        
        if (activeWeapon.getEnchantment() instanceof HasAccuracy enchantmentWithAccuracy){
            accuracy += enchantmentWithAccuracy.getAccuracy();
        }

        for (Status status : entity.getStatuses()) {
            if (status instanceof HasAccuracy accuracyStatus){
                accuracy += accuracyStatus.getAccuracy();
            }
        }
        return accuracy;
    }

    public void doOnHitted(Entity entity, AttackInfo info){
        
    }
	
	public int getSIZE_X() {
		return SIZE_X;
	}

	public int getSIZE_Y() {
		return SIZE_Y;
	}

}
