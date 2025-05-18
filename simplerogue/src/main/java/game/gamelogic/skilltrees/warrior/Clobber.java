package game.gamelogic.skilltrees.warrior;

import game.Dungeon;
import game.display.Display;
import game.gamelogic.abilities.Ability;
import game.gamelogic.behavior.Behavable;
import game.gamelogic.floorinteraction.SimpleSelector;
import game.gameobjects.DamageType;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;

public class Clobber implements Ability, Behavable{

    private int cooldown = 0;

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
        Display.getRootMenu().startSelecting(new ClobberSelector());
    }

    @Override
    public boolean isEnabled() {
        return cooldown <= 0;
    }

    private class ClobberSelector implements SimpleSelector {

        @Override
        public boolean simpleSelect(Space space) {
            if (space.isOccupied() && space.getOccupant() instanceof Animal) {
                Entity clobberee = space.getOccupant();
                int xOffset = clobberee.getX() - Dungeon.getCurrentFloor().getPlayer().getX();
                int yOffset = clobberee.getY() - Dungeon.getCurrentFloor().getPlayer().getY();
                for (int i = 0; i < 2; i++) {
                    Space nextSpace = Dungeon.getCurrentFloor().getSpace(
                        clobberee.getX() + xOffset,
                        clobberee.getY() + yOffset
                    );
                    if (nextSpace.isOccupied()) {
                        clobberee.dealDamage(2, DamageType.BLUNT);
                        break;
                    } else {
                        Space.moveEntity(clobberee, nextSpace);
                    }
                }
            }
            Clobber.this.cooldown = 50;
            return true;
        }
        
    }
    
}
