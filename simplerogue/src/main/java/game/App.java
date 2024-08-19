package game;

import game.display.Display;
import game.floorgeneration.Pools;

public class App 
{
    public static void main(String[] args){
        Pools.inintialize();
        Dungeon.initialize(50, 50);
        Display.initialize(70, 61);
        System.out.println("test");
    }

    public static int randomNumber(int min, int max){
        return (int)Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static double lerp (double x1, double y1, double x2, double y2, double x){
        return y1 + (x - x1) * ((y2-y1)/(x2-x1));
    }
    
}
