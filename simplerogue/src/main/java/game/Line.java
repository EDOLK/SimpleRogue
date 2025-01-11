package game;

import java.util.ArrayList;
import java.util.List;

import game.gameobjects.Space;

public final class Line {

    private static class Point{
        double x;
        double y;
        private Point(double x, double y){
            this.x = x;
            this.y = y;
        }
    }
    
    public static List<Space> getLineAsListInclusive(Space p0, Space p1){
        return getLineAsListInclusive(p0,p1,Dungeon.getCurrentFloor().getSpaces());
    }

    public static List<Space> getLineAsListExclusive(Space p0, Space p1){
        return getLineAsListExclusive(p0,p1,Dungeon.getCurrentFloor().getSpaces());
    }

    public static List<Space> getLineAsListInclusive(Space p0, Space p1, Space[][] spaces){
        List<Point> pointList = line(new Point(p0.getX(), p0.getY()), new Point(p1.getX(), p1.getY()));
        List<Space> spaceList = new ArrayList<Space>();
        for (int i = 0; i < pointList.size(); i++) {
            spaceList.add(spaces[(int)pointList.get(i).x][(int)pointList.get(i).y]);
        }
        return spaceList;
    }

    public static List<Space> getLineAsListExclusive(Space p0, Space p1, Space[][] spaces){
        List<Point> pointList = line(new Point(p0.getX(), p0.getY()), new Point(p1.getX(), p1.getY()));
        List<Space> spaceList = new ArrayList<Space>();
        for (int i = 1; i < pointList.size()-1; i++) {
            spaceList.add(spaces[(int)pointList.get(i).x][(int)pointList.get(i).y]);
        }
        return spaceList;
    }


    private static List<Point> line(Point p0, Point p1){
        List<Point> pointList = new ArrayList<Point>();
        double N = diagonalDistance(p0, p1);
        for (int step = 0; step <= N; step++){
            double t = 0;
            if (N == 0){
                t = 0.0;
            } else {
                t = step/N;
            }
            pointList.add(roundPoint(lerpPoint(p0, p1, t),p0));
        }
        return pointList;
    }
    
    private static double diagonalDistance(Point p0, Point p1){
        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

    private static Point roundPoint(Point p, Point relative){
        return new Point(Math.round(p.x),Math.round(p.y));
    }

    private static Point lerpPoint(Point p0, Point p1, double t){
        return new Point(lerp(p0.x, p1.x, t), lerp(p0.y, p1.y, t));
    }

    private static double lerp(double start, double end, double t){
        return start * (1.0 - t) + t * end;
    }

}
