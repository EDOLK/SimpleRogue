package game.gameobjects.entities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hexworks.zircon.api.color.TileColor;

import game.PathConditions;
import game.gamelogic.AttributeMap;
import game.gamelogic.HasAttributes;
import game.gamelogic.HasMemory;
import game.gamelogic.IsDeterrent;
import game.gamelogic.IsForbidden;
import game.gamelogic.abilities.HasPassives;
import game.gamelogic.abilities.Passive;
import game.gamelogic.behavior.AnimalWandering;
import game.gamelogic.behavior.Behavior;
import game.gamelogic.behavior.HasBehavior;
import game.gamelogic.behavior.HasEnemies;
import game.gamelogic.combat.Attack;
import game.gamelogic.combat.AttackModifier;
import game.gamelogic.combat.PostAttackHook;
import game.gamelogic.passives.MemoryDetector;
import game.gamelogic.passives.MemoryIncrementor;
import game.gameobjects.statuses.Sleeping;

public abstract class Animal extends Entity implements HasBehavior, HasAttributes, HasEnemies, HasMemory, HasPassives, AttackModifier{

    private Behavior behavior;
    private AttributeMap aMap = new AttributeMap();

    public Animal(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
        setNightVisionRange(5);
        setBehavior(getDefaultBehavior());
        addStatus(new Sleeping());
        //TODO: might roll these into a single passive later, idk
        addPassive(new MemoryIncrementor(this));
        addPassive(new MemoryDetector(this));
    }

    protected Behavior getDefaultBehavior(){
        return new AnimalWandering(this);
    }

    @Override
    public Behavior getBehavior() {
        return this.behavior;
    }

    @Override
    public boolean setBehavior(Behavior behavior) {
        this.behavior = behavior;
        return true;
    }

    @Override
    public AttributeMap getAttributeMap() {
        return this.aMap;
    }

    @Override
    public boolean isEnemy(Entity entity){
        return entity instanceof PlayerEntity;
    }

    public PathConditions getConditionsToSpace(){
        return new PathConditions()
            .addDeterrentConditions(
                (space) -> {
                    double det = 0.0d;
                    List<IsDeterrent> deterrents = space.getTerrains().stream()
                        .filter((terrain) -> terrain instanceof IsDeterrent)
                        .map(terrain -> (IsDeterrent)terrain)
                        .collect(Collectors.toList());
                    for (IsDeterrent isDeterrent : deterrents) {
                        det += isDeterrent.getDeterrent(this);
                    }
                    return det;
                }
            )
            .addForbiddenConditions(
                (space) -> {
                    boolean forb = false;
                    List<IsForbidden> forbiddens = space.getTerrains().stream()
                        .filter(terrain -> terrain instanceof IsForbidden)
                        .map(terrain -> (IsForbidden)terrain)
                        .collect(Collectors.toList());
                    for (IsForbidden isForbidden : forbiddens) {
                        forb = forb | isForbidden.getForbidden(this);
                    }
                    return forb;
                }
            );
    }

    public PathConditions getConditionsToEntity(){
        return this.getConditionsToSpace();
    }
    
    public int setAndBehave(Behavior newBehavior){
        this.setBehavior(newBehavior);
        if (this.isActive()) {
            return this.behave();
        }
        return this.getTimeToWait();
    }

    private Map<Entity, Integer> memoryMap = new HashMap<>();

    @Override
    public void addToMemory(Entity entity){
        memoryMap.computeIfPresent(entity, (k,v) -> 0);
        memoryMap.putIfAbsent(entity, 0);
    }

    @Override
    public Optional<Integer> getFromMemory(Entity entity){
        if (memoryMap.containsKey(entity)) {
            return Optional.of(memoryMap.get(entity));
        }
        return Optional.empty();
    }

    @Override
    public void incrementMemory(){
        Set<Entity> toBeRemoved = new HashSet<>();
        Set<Entity> toBeIncremented = new HashSet<>();
        for (Entry<Entity, Integer> entry : memoryMap.entrySet()) {
            if (entry.getValue() >= this.getMaxMemoryTime()){
                toBeRemoved.add(entry.getKey());
                continue;
            } 
            toBeIncremented.add(entry.getKey());
        }
        toBeIncremented.forEach((e) -> {
            memoryMap.compute(e, (k,v) -> ++v);
        });
        toBeRemoved.forEach(memoryMap::remove);
    }

    private List<Passive> passives = new ArrayList<>();

    @Override
    public List<Passive> getPassives(){
        return this.passives;
    };

    @Override
    public boolean addPassive(Passive passive){
        return this.passives.add(passive);
    };

    @Override
    public boolean removePassive(Passive passive){
        return this.passives.remove(passive);
    };

    @Override
    public void modifyAttack(Attack attack){
        attack.attachPostAttackHook((attackResult) -> {
            if (getFromMemory(attackResult.attacker()).isEmpty())
                addToMemory(attackResult.attacker());
        }, PostAttackHook.onHitted(this));
    }

}
