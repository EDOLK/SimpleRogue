package game.display;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.hexworks.zircon.api.builder.modifier.BorderBuilder;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.modifier.Border;
import org.hexworks.zircon.api.modifier.BorderType;
import org.hexworks.zircon.api.modifier.Modifier;

import game.gamelogic.Examinable;
import game.gameobjects.DisplayableTile;
import game.gameobjects.Space;
import game.gameobjects.items.Item;
import game.gameobjects.terrains.Terrain;

public class Cursor extends DisplayableTile{

    private Space selectedSpace;
    private ArrayList<Examinable> examinables = new ArrayList<Examinable>();
    private int examinablesIndex;
    private Examinable examined;

    public Cursor(Space selectedSpace){
        setSelectedSpace(selectedSpace);
        setCharacter(' ');
        setbGColor(TileColor.transparent());
        setfGColor(TileColor.transparent());
        Set<Modifier> modSet = new HashSet<Modifier>();
        Border border = BorderBuilder.newBuilder()
            .withBorderColor(TileColor.create(205, 103, 0, 255))
            .withBorderType(BorderType.DOTTED)
            .build();
        modSet.add(border);
        setModifiers(modSet);
    }
    public Examinable getExamined() {
        return examined;
    }
    public void setExamined(Examinable examined) {
        this.examined = examined;
    }
    public Space getSelectedSpace() {
        return selectedSpace;
    }
    public void setSelectedSpace(Space selectedSpace) {
        this.selectedSpace = selectedSpace;
    }
    public ArrayList<Examinable> getExaminables() {
        return examinables;
    }

    public void setExaminables(ArrayList<Examinable> examinables) {
        this.examinables = examinables;
    }

    public void collectExaminables(){
        if (selectedSpace.getLight() == 0){
            examined = null;
            return;
        }
        examinables = new ArrayList<Examinable>();
        if (selectedSpace.isOccupied()){
            examinables.add((Examinable)selectedSpace.getOccupant());
        }
        if (selectedSpace.getItems().size() != 0){
            for (Item item : selectedSpace.getItems()) {
                examinables.add((Examinable)item);
            }
        }
        if (selectedSpace.getTerrains().size() != 0){
            for (Terrain environment : selectedSpace.getTerrains()) {
                if (environment instanceof Examinable examinableEnvironment){
                    examinables.add((Examinable)examinableEnvironment);
                }
            }
        }
        if (examinables.size() != 0){
            this.examined = examinables.get(0);
        } else {
            examined = null;
        }
        examinablesIndex = 0;
    }

    public void nextExaminable(){
        examinablesIndex++;
        if (examinablesIndex >= examinables.size()){
            examinablesIndex = 0;
        }
        this.examined = examinables.get(examinablesIndex);
    }
    
    public void previousExaminable(){
        examinablesIndex--;
        if (examinablesIndex < 0){
            examinablesIndex = examinables.size()-1;
        }
        this.examined = examinables.get(examinablesIndex);
    }
}
