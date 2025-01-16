package game;

import game.Path.PathNotFoundException;
import game.gameobjects.Space;

public class PathFinder{

    private Space[] pathToDestination;
    private int placeInPath;
    private PathConditions pathConditions;

    public PathFinder(Space from, Space to) throws PathNotFoundException{
        generatePath(from, to, null);
    }

    public PathFinder(Space from, Space to, PathConditions conditions) throws PathNotFoundException{
        generatePath(from, to, conditions);
    }

    private void generatePath(Space from, Space to, PathConditions conditions) throws PathNotFoundException{
        placeInPath = 0;
        pathToDestination = Path.getPathAsArray(from, to, conditions);
        pathConditions = conditions;
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
            if (pathToDestination[i].isOccupied() || (pathConditions != null && pathConditions.evaluateForForbidden(pathToDestination[i]))){
                return false;
            }
        }
        return true;
    }

    public boolean pathHasEnded(){
        return placeInPath == pathToDestination.length-1;
    }

    public Space[] getPath() {
        return pathToDestination;
    }

}
