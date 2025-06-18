package game.gameobjects.terrains;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import game.App;
import game.gamelogic.SelfAware;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import kotlin.Pair;

public abstract class SpreadableTerrain extends Terrain implements SelfAware, Behavable {

    public static final int MAX_AMOUNT = 10;
    protected Space space;

    protected float spreadFactor = 1.0f;
    protected int spreadTime = 100;
    protected int amount;
    protected int disapparationTimer;
    protected int disapparationRate;
    protected int minSpreadAmount;

    public float getSpreadFactor() {
        return spreadFactor;
    }

    public void setSpreadFactor(float spreadFactor) {
        this.spreadFactor = spreadFactor;
    }

    public int getSpreadTime() {
        return spreadTime;
    }

    public void setSpreadTime(int spreadTime) {
        this.spreadTime = spreadTime;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount > SpreadableTerrain.MAX_AMOUNT) {
            this.amount = SpreadableTerrain.MAX_AMOUNT;
        } else if (amount < 0) {
            this.amount = 0;
        } else { 
            this.amount = amount;
        }
    }

    public int getDisapparationRate() {
        return disapparationRate;
    }

    public void setDisapparationRate(int disapparationRate) {
        this.disapparationRate = disapparationRate;
    }

    public int getMinSpreadAmount() {
        return minSpreadAmount;
    }

    public void setMinSpreadAmount(int minSpreadAmount) {
        this.minSpreadAmount = minSpreadAmount;
    }

    protected abstract SpreadableTerrain createSelf(int amount);

    protected abstract boolean isValidToSpread(Space potentialSpace);

    public SpreadableTerrain(int amount) {
        super();
        this.amount = amount;
    }

    @Override
    public int behave() {

        if (disapparationTimer >= getDisapparationRate()){
            setAmount(getAmount()-1);
            this.disapparationTimer = 0;
        } else {
            this.disapparationTimer++;
        }

        List<Space> validSpaces = Space.getAdjacentSpaces(this.getSpace()).stream()
            .filter(this::isValidToSpread)
            .collect(Collectors.toList());

        if (Math.random() < getSpreadFactor() && validSpaces != null && !validSpaces.isEmpty()) {
            while (getAmount() >= getMinSpreadAmount()) {
                Pair<Integer, List<Space>> lPair = getLowest(validSpaces, this::getAmountOfSelf);
                if (lPair.getFirst() < this.getAmount() && !lPair.getSecond().isEmpty()) {
                    this.addSelf(App.getRandom(lPair.getSecond()));
                    this.setAmount(getAmount()-1);
                } else {
                    break;
                }
            }
        }

        if (this.amount == 0) {
            this.getSpace().remove(this);
        }

        return this.spreadTime;
    }

    @Override
    public boolean isActive() {
        return getSpace() != null;
    }

    @Override
    public Space getSpace() {
        return this.space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    public Optional<SpreadableTerrain> getSelf(Space space){
        for (Terrain t : space.getTerrains()) {
            if (this.getClass().isInstance(t)) {
                return Optional.of((SpreadableTerrain)t);
            }
        }
        return Optional.empty();
    }

    protected int getAmountOfSelf(Space space) {
        Optional<SpreadableTerrain> selfOpt = getSelf(space);
        if (selfOpt.isPresent()) {
            return selfOpt.get().getAmount();
        }
        return 0;
    }

    private void addSelf(Space space){
        Optional<SpreadableTerrain> optSelf = getSelf(space);
        if (optSelf.isPresent()) {
            optSelf.get().setAmount(optSelf.get().getAmount()+1);
        } else {
            space.addTerrain(createSelf(1));
        }
    }

    private <T> Pair<Integer, List<T>> getLowest (List<T> list, Function<T,Integer> func){
        int lowestInt = Integer.MAX_VALUE;
        List<T> lowestList = new ArrayList<>();
        for (T t : list) {
            int val = func.apply(t);
            if (val < lowestInt) {
                lowestInt = val;
                lowestList = new ArrayList<>(List.of(t));
            } else if (val == lowestInt){
                lowestList.add(t);
            }
        }
        return new Pair<Integer, List<T>>(lowestInt, lowestList);
    }
    
}
