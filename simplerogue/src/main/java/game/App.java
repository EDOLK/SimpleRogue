package game;

import game.display.Display;
import game.floorgeneration.Pools;
import game.gameobjects.DamageType;
import game.gameobjects.items.Torch;
import game.gameobjects.items.weapons.Dagger;

public class App 
{
    public static void main(String[] args){
        Pools.initialize();
        Dungeon.initialize(50, 50);
        Display.initialize(70, 61);
        Dungeon.getCurrentFloor().getPlayer().addItemToInventory(new Torch());
    }

    public static int randomNumber(int min, int max){
        return (int)Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static double lerp (double x1, double y1, double x2, double y2, double x){
        return y1 + (x - x1) * ((y2-y1)/(x2-x1));
    }
    
}
