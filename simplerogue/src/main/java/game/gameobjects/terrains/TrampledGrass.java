package game.gameobjects.terrains;

public class TrampledGrass extends Moss{

    public TrampledGrass() {
        super();
        setfGColor(this.getfGColor().darkenByPercent(0.20));
    }

    @Override
    public String getName() {
        return "Trampled Grass";
    }

    @Override
    public String getDescription() {
        return "Grass thats been trampled. Someones been here...";
    }
    
}
