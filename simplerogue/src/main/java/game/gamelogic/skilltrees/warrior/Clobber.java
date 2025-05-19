package game.gamelogic.skilltrees.warrior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.App;
import game.Dungeon;
import game.display.Display;
import game.gamelogic.Armed;
import game.gamelogic.OverridesBehavable;
import game.gamelogic.abilities.Ability;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.floorinteraction.SimpleSelector;
import game.gameobjects.AttackResult;
import game.gameobjects.DamageType;
import game.gameobjects.Floor;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;
import game.gameobjects.items.weapons.Weapon;
import game.gameobjects.statuses.Status;

public class Clobber implements Ability, Behavable{

    private int cooldown = 0;
    private Entity owner;

    public Clobber(Entity owner) {
        this.owner = owner;
    }

    @SuppressWarnings("unused")
    private Clobber(){}

    @Override
    public String getName() {
        if (cooldown > 0) {
            return "Clobber " + "(" + cooldown + ")";
        }
        return "Clobber";
    }

    @Override
    public int behave() {
        if (cooldown > 0) {
            cooldown--;
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return cooldown > 0;
    }

    @Override
    public void activate() {
        Display.getRootMenu().startSelecting(new ClobberSelector(owner));
    }

    @Override
    public boolean isEnabled() {
        return cooldown <= 0;
    }

    private class ClobberSelector implements SimpleSelector {

        private Entity owner;

        private ClobberSelector(Entity owner) {
            this.owner = owner;
        }

        private ClobberSelector(){};

        @Override
        public boolean simpleSelect(Space space) {

            List<Weapon> validWeapons = new ArrayList<>();

            if (owner instanceof Armed armedOwner) {
                for (Weapon weapon : armedOwner.getWeapons()) {
                    if (weapon.getDamageType() == DamageType.BLUNT) {
                        validWeapons.add(weapon);
                    }
                }
            }

            if (validWeapons.isEmpty() && owner.getUnarmedWeapon() != null && owner.getUnarmedWeapon().getDamageType() == DamageType.BLUNT) {
                validWeapons.add(owner.getUnarmedWeapon());
            }

            if (validWeapons.isEmpty()) {
                Display.log("No valid weapons!");
                return true;
            }

            Weapon weaponToUse = App.getRandom(validWeapons);

            if (space.isOccupied() && space.getOccupant() instanceof Animal) {
                Entity clobberee = space.getOccupant();
                int xOffset = clobberee.getX() - Dungeon.getCurrentFloor().getPlayer().getX();
                int yOffset = clobberee.getY() - Dungeon.getCurrentFloor().getPlayer().getY();
                Space nextSpace = Dungeon.getCurrentFloor().getSpace(
                    clobberee.getX() + xOffset,
                    clobberee.getY() + yOffset
                );

                AttackResult result = Floor.doAttack(owner, clobberee, weaponToUse);

                if (result.hit()) {
                    if (nextSpace.isOccupied()) {
                        clobberee.dealDamage(2, DamageType.BLUNT);
                    } else {
                        Space.moveEntity(clobberee, nextSpace);
                    }
                    clobberee.addStatus(new Dazed());
                }
                Clobber.this.cooldown = 50;
            }
            return true;
        }
        
    }

    private class Dazed extends Status implements Behavable, OverridesBehavable{

        private int randNum = App.randomNumber(1,5);
        private int timer = 3;

        public Dazed() {
            super();
            this.setCharacter('*');
            this.setfGColor(TileColor.create(252,252,76, 255));
            this.setDescriptor("Dazed");
            Set<Modifier> set = new HashSet<>();
            set.add(Modifiers.blink());
            this.setModifiers(set);
        }

        @Override
        public int behave() {
            timer--;
            if (timer <= 0) {
                this.owner.removeStatus(this);
            }
            return 100;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public int overrideBehave(Behavable behavable) {
            return behavable.behave();
            //if (randNum == 1) {
            //    return behavable.behave();
            //} else {
            //    List<Space> adjacentSpaces = Space.getAdjacentSpaces(this.owner.getSpace());
            //    Iterator<Space> it = adjacentSpaces.iterator();
            //    while (it.hasNext()) {
            //        Space space = it.next();
            //        if (space.isOccupied()) {
            //            it.remove();
            //        }
            //    }
            //    if (!adjacentSpaces.isEmpty() && App.randomNumber(0,1) == 0) {
            //        Space.moveEntity(this.owner, App.getRandom(adjacentSpaces));
            //        return this.owner.getTimeToMove();
            //    }
            //    Display.log("The " + this.owner.getName() + " sways arround dizzily.", owner.getSpace());
            //    return this.owner.getTimeToWait();
            //}
        }

        @Override
        public boolean overrideIsActive(Behavable behavable) {
            return behavable.isActive();
            //randNum = App.randomNumber(1,5);
            //if (randNum == 1) {
            //    return behavable.isActive();
            //}
            //return true;
        }

    }
    
}
