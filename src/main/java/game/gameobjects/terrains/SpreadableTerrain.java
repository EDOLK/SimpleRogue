package game.gameobjects.terrains;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hexworks.zircon.api.data.Tile;

import game.App;
import game.display.Display;
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

    public SpreadableTerrain(int amount) {
        super();
        setAmount(amount);
    }

    public int getDisapparationTimer() {
        return disapparationTimer;
    }

    public void setDisapparationTimer(int disapparationTimer) {
        this.disapparationTimer = disapparationTimer;
    }

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

    @Override
    public int behave() {

        if (this.getDisapparationTimer() >= this.getDisapparationRate()){
            this.setAmount(this.getAmount()-1);
            this.setDisapparationTimer(0);
        } else {
            this.setDisapparationTimer(this.getDisapparationTimer()+1);
        }

        List<Space> validSpaces = Space.getAdjacentSpaces(this.getSpace()).stream()
            .filter(this::isValidToSpread)
            .collect(Collectors.toList());

        if (Math.random() < this.getSpreadFactor() && validSpaces != null && !validSpaces.isEmpty()) {
            while (this.getAmount() >= this.getMinSpreadAmount()) {
                Pair<Integer, List<Space>> lPair = getLowest(validSpaces);
                Space lowestSpace = App.getRandom(lPair.getSecond());
                if (lPair.getFirst() < this.getAmount() && lowestSpace != null) {
                    this.setAmount(this.getAmount()-1);
                    lowestSpace.addTerrain(this.createSelfWithTimer(1));
                } else {
                    break;
                }
            }
        }

        if (this.getAmount() <= 0) {
            this.getSpace().remove(this);
        }

        return this.getSpreadTime();
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

    protected abstract boolean isValidToSpread(Space potentialSpace);

    protected abstract SpreadableTerrain createSelf(int amount);

    protected SpreadableTerrain createSelfWithTimer(int amount){
        SpreadableTerrain t = createSelf(amount);
        t.setDisapparationTimer(this.getDisapparationTimer());
        return t;
    }

    protected Pair<Integer, List<Space>> getLowest(List<Space> list){
        int lowestInt = Integer.MAX_VALUE;
        List<Space> lowestList = new ArrayList<>();
        for (Space t : list) {
            int val = this.getAmountOfSelf(t);
            if (val < lowestInt) {
                lowestInt = val;
                lowestList = new ArrayList<>(List.of(t));
            } else if (val == lowestInt){
                lowestList.add(t);
            }
        }
        return new Pair<Integer, List<Space>>(lowestInt, lowestList);
    }

    @Override
    public Tile getTile(double percent) {
        return getTile(percent, amount);
    }

    public Tile getTile(double percent, int amount){
        int quotient = MAX_AMOUNT/3;
        int alpha = (int)App.lerp(0,140,SpreadableTerrain.MAX_AMOUNT,255,amount);
        int value = (int)App.lerp(0, 0, SpreadableTerrain.MAX_AMOUNT, 1, amount);
        switch (Display.getMode()) {
            case ASCII:
                return Tile.newBuilder()
                    .withBackgroundColor(getbGColor().darkenByPercent(percent).withAlpha(alpha))
                    .withForegroundColor(getfGColor().darkenByPercent(percent).lightenByPercent(value).withAlpha(255))
                    .withCharacter(
                        amount <= quotient ? '░' :
                        amount <= (quotient*2) ? '▒' :
                        '▓'
                    )
                    .withModifiers(getModifiers())
                    .withTileset(Display.getGraphicalTileSet())
                    .build();
            case GRAPHICAL:
                return Tile.newBuilder()
                    .withName(getTileName())
                    .withTileset(Display.getGraphicalTileSet())
                    .buildGraphicalTile();
            default:
                return Tile.defaultTile();
        }
    }

}
