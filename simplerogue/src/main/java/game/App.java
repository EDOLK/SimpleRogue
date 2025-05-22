package game;

import java.util.List;

import game.display.Display;
import game.gameobjects.items.armor.Chainmail;
import game.gameobjects.items.armor.PlateArmor;

public class App 
{
    public static void main(String[] args){
        Dungeon.initialize(50, 50);
        Display.initialize(70, 61);
        Dungeon.getCurrentFloor().getPlayer().addItemToInventory(new PlateArmor());
        Dungeon.getCurrentFloor().getPlayer().addItemToInventory(new Chainmail());
    }

    public static int randomNumber(int min, int max){
        return (int)Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static double randomNumber(double min, double max){
        return Math.random() * (max - min) + min;
    }

    public static double lerp (double x1, double y1, double x2, double y2, double x){
        return y1 + (x - x1) * ((y2-y1)/(x2-x1));
    }
    
    public static <T> T getRandom(List<T> list){
        return list.isEmpty() ? null : list.get(randomNumber(0,list.size()-1));
    }

}
