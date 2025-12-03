package game.gamelogic;

import game.gameobjects.Space;

public class Angle {
    private int degree = 0;

    public Angle(int degrees){
        setDegree(degrees);
    }

    public int getDegree() {
        return degree;
    }

    public boolean setDegree(int degree) {
        degree = degree % 360;
        degree = degree < 0 ? degree + 360 : degree;
        this.degree = degree;
        return true;
    }

    public static Angle of(Space space1, Space space2){
        double x = (double)(space1.getX() - space2.getX())*-1;
        double y = space1.getY() - space2.getY();
        return new Angle((int)Math.toDegrees(Math.atan2(y,x)));
    }

}
