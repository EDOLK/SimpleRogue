package game.gamelogic;

import java.util.List;

import game.gameobjects.AttackResult;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.weapons.Weapon;

public interface OverridesAttack{
    public List<AttackResult> overrideAttack(Entity attacker, Entity attackee);
    public AttackResult overrideAttack(Entity attacker, Entity attackee, Weapon attackerWeapon);
}
