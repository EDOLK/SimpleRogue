package game.gameobjects.enchantments;

import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Tile;

import game.Dungeon;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnDeath;
import game.gamelogic.combat.OnHitted;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.Item;
import game.gameobjects.items.weapons.Weapon;

public class Clotting extends ArmorEnchantment implements OnHitted {

    private class BloodPolyp extends Entity implements Behavable, OnDeath{

        private int healthStored;
        private Entity owner;

        public BloodPolyp(int healthStored, Entity owner) {

            super(TileColor.transparent(), TileColor.create(200, 25, 25, 255), 'o');
            this.healthStored = healthStored;
            this.owner = owner;
            setMaxHP(1);
            setHP(1);
            setName("Blood Polyp");
            setDescription("A floating polyp of coagulated blood.");
            setWeight(1);

            Weapon mass = new Weapon();
            mass.setName("mass");
            mass.setDamageType(DamageType.BLUNT);
            mass.setDamage(0, 0);
            setUnarmedWeapon(mass);

        }

        @Override
        public Item getCorpse() {
            return null;
        }

        @Override
        public void doOnDeath(Entity self, Entity other, AttackInfo attackInfo) {
            if (other == owner){
                owner.heal(healthStored);
            }
        }

        @Override
        public int behave() {
            if (randomNumber(1,10) <= 5){
                List<Space> adjacentSpaces = Space.getAdjacentSpaces(this.getSpace()).stream().filter((s) -> !s.isOccupied()).collect(Collectors.toList());
                if (adjacentSpaces.size() != 0){
                    Space.moveEntity(this,adjacentSpaces.get(randomNumber(0,adjacentSpaces.size()-1)));
                }
            }
            return this.getTimeToMove();
        }

        @Override
        public boolean isActive() {
            return isAlive();
        }
        
    }

    public Clotting(){
        this.prefix = "Clotting";
    }

    @Override
    public void doOnHitted(Entity self, Entity other, AttackInfo attackInfo) {
        if (randomNumber(1,5) == 1 && attackInfo.getDamageDelt() != 0){
            List<Space> adjacentSpaces = Space.getAdjacentSpaces(self.getSpace()).stream().filter((s) -> !s.isOccupied()).collect(Collectors.toList());
            if (adjacentSpaces.size() != 0){
                adjacentSpaces.get(
                    randomNumber(0,adjacentSpaces.size()-1)
                ).setOccupant(
                    new BloodPolyp(attackInfo.getDamageDelt(), self)
                );
            }
        }
    }

    @Override
    public String getDescription() {
        return "This enchantment instills the users blood with a strange energy. When attacked, the users blood has a small chance to coagulate into a floating blood polyp, which will heal the user when destroyed.";
    }

    @Override
    public Tile getTile() {
        return new BloodPolyp(0,null).getTile();
    }

    @Override
    public String getName() {
        return "Clotting";
    }
    
}
