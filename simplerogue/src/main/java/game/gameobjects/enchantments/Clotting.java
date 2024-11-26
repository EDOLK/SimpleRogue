package game.gameobjects.enchantments;

import static game.App.randomNumber;

import java.util.ArrayList;
import java.util.List;

import org.hexworks.zircon.api.color.TileColor;

import game.Dungeon;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHitted;
import game.gameobjects.DamageType;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.weapons.Weapon;

public class Clotting extends ArmorEnchantment implements OnHitted {

    private class BloodPolyp extends Entity implements Behavable {

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
            setCorpse(null);

            Weapon mass = new Weapon();
            mass.setName("mass");
            mass.setDamageType(DamageType.BLUNT);
            mass.setDamage(0, 0);
            setUnarmedWeapon(mass);

        }

        @Override
        public void defaultInteraction(Entity interactor) {
            Floor.doAttack(interactor, this);
        }

        @Override
        public void onKill(Entity killer) {
            super.onKill(killer);
            if (killer == owner){
                owner.heal(healthStored);
            }
        }

        @Override
        public void behave() {
            if (randomNumber(1,10) <= 5){
                int cX = this.getX();
                int cY = this.getY();
                List<Space> adjacentSpaces = new ArrayList<>();
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        if (x == 0 && y == 0)
                            continue;
                        adjacentSpaces.add(Dungeon.getCurrentFloor().getSpace(cX + x, cY + y));
                    }
                }
                for (int i = 0; i < adjacentSpaces.size(); i++) {
                    Space s = adjacentSpaces.get(i);
                    if (s.isOccupied()){
                        adjacentSpaces.remove(s);
                        i--;
                    }
                }
                if (adjacentSpaces.size() != 0){
                    Space.moveEntity(this,adjacentSpaces.get(randomNumber(0,adjacentSpaces.size()-1)));
                }
            }
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
    public void activate(Entity self, Entity other, AttackInfo attackInfo) {
        if (randomNumber(1,5) == 1 && attackInfo.getDamageDelt() != 0){
            int cX = self.getX();
            int cY = self.getY();
            List<Space> adjacentSpaces = new ArrayList<>();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0)
                        continue;
                    adjacentSpaces.add(Dungeon.getCurrentFloor().getSpace(cX + x, cY + y));
                }
            }
            for (int i = 0; i < adjacentSpaces.size(); i++) {
                Space s = adjacentSpaces.get(i);
                if (s.isOccupied()){
                    adjacentSpaces.remove(s);
                    i--;
                }
            }
            if (adjacentSpaces.size() != 0){
                adjacentSpaces.get(
                    randomNumber(0,adjacentSpaces.size()-1)
                ).setOccupant(
                    new BloodPolyp(attackInfo.getDamageDelt(), self)
                );
            }
        }
    }
    
}
