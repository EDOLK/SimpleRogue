package game.gameobjects.terrains;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.gamelogic.Examinable;
import game.gamelogic.Flammable;
import game.gamelogic.interactions.HasInteraction;
import game.gamelogic.interactions.Interaction;
import game.gamelogic.interactions.InteractionResult;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Door;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Burning;

public class OpenDoor extends Terrain implements Examinable, Flammable, HasInteraction{
    
    private Door door;

    public OpenDoor(Door door){
        super(TileColor.transparent(), TileColor.create(181, 88, 45, 255), '\\');
        setTileName("Open Door");
        this.door = door;
    }

    public int closeDoor(Entity interactor){
        if (door.getSpace().isOccupied()){
            Display.log("Something is in the way.", door.getSpace());
            return 0;
        }
        door.getSpace().removeTerrain(this);
        door.getSpace().setOccupant(door);
        Display.update();
        return interactor.getTimeToMove();
    }

    @Override
    public Interaction getInteraction() {
        return new Interaction.Builder()
            .withName("Close")
            .withOnInteract((interactor) -> {
                return InteractionResult.create()
                    .withTimeTaken(closeDoor(interactor))
                    .withRevertMenu();
            })
            .build();
    }

    @Override
    public String getName() {
        return "Open " + door.getName();
    }

    @Override
    public String getDescription() {
        return door.getDescription();
    }

    @Override
    public int getFuelValue() {
        return 1;
    }

    @Override
    public void onBurn() {
        door.addStatus(new Burning());
        int damage = randomNumber(1, 3);
        if (door.getHP() - damage <= 0){
            door.getSpace().removeTerrain(this);
        }
        door.dealDamage(damage, DamageType.FIRE);
    }

}
