package game;

import game.Path.PathNotFoundException;
import game.gameobjects.Space;

public class PathFinder{

    private Space[] pathToDestination;
    private int placeInPath;

    public PathFinder(Space from, Space to) throws PathNotFoundException{
        generatePath(from, to);
    }

    private void generatePath(Space from, Space to) throws PathNotFoundException{
        placeInPath = 0;
        pathToDestination = Path.getPathAsArray(from, to);
    }
    
    public Space getNext(){
        iterate();
        return getSpace();
    }
    
    public void iterate(){
        placeInPath++;
    }

    public Space getSpace(){
        return pathToDestination[placeInPath];
    }

    public boolean pathIsClear(){
        for (int i = placeInPath+1; i < pathToDestination.length-1; i++) {
            if (pathToDestination[i].isOccupied()){
                return false;
            }
        }
        return true;
    }

    public boolean pathHasEnded(){
        return placeInPath == pathToDestination.length-1;
    }
}
