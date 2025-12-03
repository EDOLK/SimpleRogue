package game.gameobjects.terrains.projectiles;

import org.hexworks.zircon.api.color.TileColor;

import game.gamelogic.Angle;
import game.gamelogic.behavior.Behavable;
import game.gameobjects.Space;
import game.gameobjects.terrains.Terrain;

public class ProjectileTrail extends Terrain implements Behavable{

    private Runnable deleteSelf;

    public ProjectileTrail(TileColor fgColor, Angle angle, Space space){
        if (fgColor != null) {
            setFgColor(fgColor);
        }
        deleteSelf = () -> {
            space.removeTerrain(this);
            this.deleteSelf = null;
        };
        int degree = angle.getDegree();
        char c = '*';
        if (withinAnEight(degree, 0) || withinAnEight(degree, 180) || withinAnEight(degree, 360)) {
            c = '-';
        } else if (withinAnEight(degree, 45) || withinAnEight(degree, 225)) {
            c = '/';
        } else if (withinAnEight(degree, 90) || withinAnEight(degree, 270)) {
            c = '|';
        } else if (withinAnEight(degree, 135) || withinAnEight(degree, 315)){
            c = '\\';
        }
        setCharacter(c);
    }

    public boolean withinAnEight(int degree, int within){
        return degree >= (within - 22.5) && degree < (within + 22.5);
    }

    @Override
    public int behave() {
        deleteSelf.run();
        return 1;
    }

    @Override
    public boolean isActive() {
        return deleteSelf != null;
    }

    
}
