package game.gameobjects.terrains;

import static game.App.randomNumber;

import org.hexworks.zircon.api.color.TileColor;

import game.display.Display;
import game.gamelogic.Examinable;
import game.gamelogic.Flammable;
import game.gamelogic.Interactable;
import game.gameobjects.DamageType;
import game.gameobjects.entities.Door;
import game.gameobjects.entities.Entity;
import game.gameobjects.statuses.Burning;

public class OpenDoor extends Terrain implements Interactable, Examinable, Flammable{
    
    private Door door;
    public String name;
    public String description;
    public String stats;

    public OpenDoor(Door door){
        super(TileColor.transparent(), TileColor.create(181, 88, 45, 255), '\\');
        setTileName("open door 1");
        this.door = door;
    }

    @Override
    public void onInteract(Entity interactor) {
        if (door.getSpace().isOccupied()){
            Display.log("Something is in the way.");
        } else {
            door.getSpace().getTerrains().remove(this);
            door.getSpace().setOccupant(door);
            Display.update();
        }
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
    public void onBurn(Fire fire) {
        door.addStatus(new Burning());
        int damage = randomNumber(1, 3);
        if (door.getHP() - damage <= 0){
            door.getSpace().getTerrains().remove(this);
        }
        door.dealDamage(damage, DamageType.FIRE);
    }

}
