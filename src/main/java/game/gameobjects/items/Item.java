package game.gameobjects.items;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Examinable;
import game.gamelogic.Levelable;
import game.gameobjects.DisplayableTile;

public class Item extends DisplayableTile implements Examinable{

    private int weight = 5;

    private String name = "Placeholder Name";

    private String description = "Placeholder description.";
    
    public Item(TileColor bGColor, TileColor fGColor, char character) {
        super(bGColor, fGColor, character);
    }

    public Item(){
        super();
    }

    public String getName() {
        String n = name;
        if (this instanceof Levelable levelableItem){
            if (levelableItem.getLevel() > 1){
                n += " +" + String.valueOf(levelableItem.getLevel()-1);
            }
        }
        return n;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    
}
