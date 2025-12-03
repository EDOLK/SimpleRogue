package game.gamelogic.floorinteraction;

import game.display.Cursor;
import game.display.Display;
import game.display.ExamineMenu;
import game.gameobjects.Space;

public class ExamineSelector implements Selector{

    @Override
    public SelectionResult select(Cursor cursor) {
        if (cursor.getExamined() != null){
            Display.setMenu(new ExamineMenu(cursor.getExamined()));
        }
        return new SelectionResult(false, 0);
    }

    @Override
    public boolean canMove(Cursor cursor, Space toSpace) {
        return true;
    }

}
