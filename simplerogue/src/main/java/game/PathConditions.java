package game;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import game.Path.Node;
import game.gameobjects.Space;

public class PathConditions {

    private List<Predicate<Space>> forbiddenConditions = new ArrayList<Predicate<Space>>(
        List.of((space) -> {
            return space.isOccupied();
        })
    );

    private List<Function<Space,Double>> deterrentConditions = new ArrayList<Function<Space,Double>>();

    private boolean diagonal = true;
  
    private BiFunction<Node,Node,Double> hFunction = (from, to) -> {
        return (double)Math.abs(to.x - from.x) + Math.abs(to.y - from.y);
    }
  
    public PathConditions() {
        super();
    }

    public boolean evaluateForForbidden(Space space){
        for (Predicate<Space> cond : forbiddenConditions) {
            if (cond.test(space)) {
                return true;
            }
        }
        return false;
    }

    public double evaluateForDeterrent(Space space){
        double score = 0;
        for (Function<Space,Double> function : deterrentConditions) {
            score += function.apply(space);
        }
        return score;
    }

    @SafeVarargs
    public final PathConditions addForbiddenConditions(Predicate<Space>... conditions){
        forbiddenConditions.addAll(List.of(conditions));
        return this;
    }

    @SafeVarargs
    public final PathConditions addDeterrentConditions(Function<Space,Double>... conditions){
        deterrentConditions.addAll(List.of(conditions));
        return this;
    }

    @SafeVarargs
    public final PathConditions removeForbiddenConditions(Predicate<Space>... conditions){
        forbiddenConditions.removeAll(List.of(conditions));
        return this;
    }

    public final PathConditions removeForbiddenCondition(int condition){
        forbiddenConditions.remove(condition);
        return this;
    }

    @SafeVarargs
    public final PathConditions removeDeterrentConditions(Function<Space,Double>... conditions){
        deterrentConditions.removeAll(List.of(conditions));
        return this;
    }

    public final PathConditions removeDeterrentCondition(int condition){
        deterrentConditions.remove(condition);
        return this;
    }

    public boolean isDiagonal(){
        return this.diagonal;
    }

    public PathConditions setDiagonal(Boolean diagonal){
        this.diagonal = diagonal;
        return this;
    }
  
    public BiFunction<Node,Node,Double> getHFunction(){
        return this.hFunction;
    }

    public PathConditions setHFunction(BiFunction<Node,Node,Double> hFunction){
        this.hFunction = hFunction;
        return this;
    }
  
}
