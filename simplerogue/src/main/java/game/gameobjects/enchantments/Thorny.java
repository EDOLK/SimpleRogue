package game.gameobjects.enchantments;

import static game.App.randomNumber;

import java.util.HashSet;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.behavior.Behavable;
import game.gamelogic.combat.AttackInfo;
import game.gamelogic.combat.OnHitted;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Status;

public class Thorny extends ArmorEnchantment implements OnHitted {

    private class Bleeding extends Status implements Behavable{

        private int remainingDamage;
        private int minDamage;
        private int maxDamage;

        public Bleeding(int damage, int minDamage, int maxDamage){
            this.remainingDamage = damage;
            this.minDamage = minDamage;
            this.maxDamage = maxDamage;
            setCharacter(' ');
            setbGColor(TileColor.transparent());
            setfGColor(TileColor.create(200, 50, 50, 100));
            HashSet<Modifier> modifiers = new HashSet<Modifier>();
            modifiers.add(Modifiers.blink());
            setModifiers(modifiers);
            setDescriptor("Bleeding");
        }

        @Override
        public int behave() {
            int damageToBeDelt = randomNumber(Math.min(minDamage,remainingDamage), Math.min(maxDamage,remainingDamage));
            if (owner.isAlive()){
                owner.dealDamage(damageToBeDelt, DamageType.BLEED);
            }
            remainingDamage -= damageToBeDelt;
            if (remainingDamage <= 0){
                owner.removeStatus(this);
            }
            return 100;
        }

        @Override
        public boolean isActive() {
            return owner != null && owner.isAlive();
        }
        
    }

    public Thorny(){
        this.prefix = "Thorny";
    }

    @Override
    public void doOnHitted(Entity self, Entity other, AttackInfo attackInfo) {
        if (randomNumber(1,5) == 5 && attackInfo.getDamageDelt() != 0){
            other.addStatus(new Bleeding((int)(attackInfo.getDamageDelt()*0.5),1,3));
        }
    }

    @Override
    public String getDescription() {
        return "This enchantment covers the user's armor with aetherial spikes. When attacked, these spikes have a chance of manifesting and causing the attacking enemy to bleed for a fraction of the damage delt.";
    }

    @Override
    public Tile getTile() {
        return Tile.empty();
    }

    @Override
    public String getName() {
        return "Thorny";
    }
    
}
