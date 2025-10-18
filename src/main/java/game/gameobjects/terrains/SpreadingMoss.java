package game.gameobjects.terrains;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.hexworks.zircon.api.Modifiers;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Modifier;

import game.display.Display;
import game.gamelogic.Examinable;
import game.gameobjects.Space;
import game.gameobjects.entities.Entity;
import game.gameobjects.entities.PlayerEntity;
import game.gameobjects.statuses.Rooted;
import game.gameobjects.terrains.liquids.Water;
import kotlin.Pair;

public class SpreadingMoss extends SpreadableTerrain implements Examinable{

    public SpreadingMoss(int amount) {
        super(amount);
        setFgColor(TileColor.create(50, 255, 50, 255));
        setBgColor(TileColor.create(255, 255, 50, 255));
        setCharacter('▓');
        setModifiers(new HashSet<Modifier>(List.of(Modifiers.blink())));
        setDisapparationRate(10);
    }

    @Override
    public String getName() {
        return "Spreading Moss";
    }

    @Override
    public String getDescription() {
        return "Moss rapidly spreading across the dungeon floor.";
    }

    @Override
    protected Pair<Integer, List<Space>> getLowest(List<Space> list) {
        Pair<Integer, List<Space>> pair = super.getLowest(list);
        int amount = pair.getFirst();
        List<Space> spaces = pair.getSecond();
        // prioritize barren tiles:
        if (spaces.stream().anyMatch(
            s -> s.getTerrains().stream().allMatch(
                t -> !(t instanceof Moss) && !(t instanceof Grass)
            )
        )) {
            spaces = spaces.stream().filter(
                s -> s.getTerrains().stream().allMatch(
                    t -> !(t instanceof Moss) && !(t instanceof Grass)
                )
            ).collect(Collectors.toCollection(() -> new ArrayList<>()));
        }
        // prioritize tiles with water on them:
        if (spaces.stream().anyMatch(
            s -> s.getTerrains().stream().anyMatch(
                t -> t instanceof Water water && water.getAmount() > 0
            )
        )) {
            spaces = spaces.stream().filter(
                s -> s.getTerrains().stream().anyMatch(
                    t -> t instanceof Water water && water.getAmount() > 0
                )
            ).collect(Collectors.toCollection(() -> new ArrayList<>()));
        }
        return new Pair<Integer, List<Space>>(amount, spaces);
    }

    @Override
    public int behave() {

        if (getSpace().isOccupied()) {
            Entity occupant = getSpace().getOccupant();
            if (occupant.addStatus(new Rooted())){
                if (occupant instanceof PlayerEntity) {
                    Display.log("You are rooted!");
                } else {
                    Display.log("The " + occupant.getOriginalName() + " is rooted.", occupant.getSpace());
                }
            }
        }

        for (Terrain terrain : getSpace().getTerrains()) {
            if (terrain instanceof Water water && water.getAmount() > 0) {
                this.setDisapparationTimer(this.getDisapparationTimer() - 2);
                water.setAmount(water.getAmount()-1);
                break;
            }
        }

        Moss.trySpread(this.getSpace(), false, false);

        return super.behave();

    }

    
    @Override
    protected boolean isValidToSpread(Space potentialSpace) {
        return !potentialSpace.isOccupied() || !potentialSpace.getOccupant().isLiquidBlocker();
    }

    @Override
    protected SpreadableTerrain createSelf(int amount) {
        return new SpreadingMoss(amount);
    }


}
