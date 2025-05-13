package game.gamelogic.behavior;

import java.util.ArrayList;
import java.util.List;

import game.App;
import game.display.Display;
import game.gamelogic.HasSkills;
import game.gamelogic.SkillMap.Skill;
import game.gameobjects.Space;
import game.gameobjects.entities.Animal;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;

public class AnimalSleeping extends Behavior{

    private Animal animal;
    private Behavior reservedBehavior;

    public AnimalSleeping(Animal animal, Behavior reservedBehavior) {
        this.animal = animal;
        this.reservedBehavior = reservedBehavior;
    }
    public AnimalSleeping(Animal animal) {
        this.animal = animal;
        this.reservedBehavior = animal.getBehavior();
        animal.setBehavior(this);
    }

    @Override
    public int behave() {
        Entity target = checkForTarget();
        if (target != null) {
            int targetStealth = App.randomNumber(1,20);
            int animalPerception = App.randomNumber(1,20);
            if (target instanceof HasSkills hasSkills) {
                targetStealth += hasSkills.getSkill(Skill.STEALTH);
            }
            if (this.animal instanceof HasSkills hasSkills) {
                animalPerception += hasSkills.getSkill(Skill.PERCEPTION);
            }
            if (animalPerception >= targetStealth) {
                Display.log("The " + animal.getName() + " wakes up.", animal.getSpace());
                this.animal.setBehavior(this.reservedBehavior);
            }
        }
        return 100;
    }

    @Override
    public boolean isActive() {
        return animal != null && animal.isAlive();
    }

    protected Entity checkForTarget(){
        for (Entity entity : getEntitiesInVision()) {
            if (entity instanceof PlayerEntity playerEntity){
                return playerEntity;
            }
        }
        return null;
    }

    protected List<Entity> getEntitiesInVision(){
        List<Entity> entityList = new ArrayList<Entity>();
        for (Space space : this.animal.getSpacesInVision()) {
            if (space.isOccupied()){
                entityList.add(space.getOccupant());
            }
        }
        return entityList;
    }
    
}
