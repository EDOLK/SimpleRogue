package game;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.function.BiFunction;

import game.gameobjects.Space;

public final class Path {

    public static class PathNotFoundException extends Exception{

        private Node destinationNode;
        private Space destinationSpace;

        public PathNotFoundException(Node destinationNode){
            this.destinationNode = destinationNode;
            this.destinationSpace = Dungeon.getCurrentFloor().getSpace(destinationNode.x, destinationNode.y);
        }

        public PathNotFoundException(Space destinationSpace){
            this.destinationSpace = destinationSpace;
        }

        public Space getDestinationSpace() {
            return destinationSpace;
        }

        public Node getDestinationNode() {
            return destinationNode;
        }
    }

    private static class Cords{
        int x;
        int y;
        public Cords(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    public static class Node implements Comparable<Node>{
        int x;
        int y;
        double g;
        double h;
        double d;
        double f;
        Cords parentCords;
        boolean passable = true;
        public Node(int x, int y){
            this.x = x;
            this.y = y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        @Override
        public int compareTo(Node o) {
            return (int)(f*100) - (int)(o.f*100);
        }
        @Override
        public String toString(){
            return "(" + x + ", " + y +")";
        }
    }

    /** Returns our path from startingSpace to destinationSpace, with startingSpace and destinationSpace included. */
    public static Space[] getPathAsArray(Space startingSpace, Space destinationSpace) throws PathNotFoundException{
        return getPathAsArray(startingSpace, destinationSpace, new PathConditions());
    }

    /** Returns our path from startingSpace to destinationSpace, with startingSpace and destinationSpace included. */
    public static Space[] getPathAsArray(Space startingSpace, Space destinationSpace, PathConditions conditions) throws PathNotFoundException{
        return getPathAsArray(startingSpace, destinationSpace, Dungeon.getCurrentFloor().getSpaces(), conditions);
    }

    /** Returns our path from startingSpace to destinationSpace, with startingSpace and destinationSpace included. */
    public static Space[] getPathAsArray(Space startingSpace, Space destinationSpace, Space[][] spaces, PathConditions conditions) throws PathNotFoundException{
        Node[][] grid = new Node[spaces.length][spaces[0].length];
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                grid[x][y] = new Node(x, y);
                Space space = spaces[x][y];
                if (space != destinationSpace && space != startingSpace){
                    if (conditions.evaluateForForbidden(space)) {
                        grid[x][y].passable = false;
                    }
                }
                grid[x][y].d = conditions != null ? conditions.evaluateForDeterrent(space) : 0;
            }
        }
        Node startingNode = grid[startingSpace.getX()][startingSpace.getY()];
        Node destinationNode = grid[destinationSpace.getX()][destinationSpace.getY()];
        try {
            Node[] nodePath = getPath(startingNode, destinationNode, grid, conditions.isDiagonal(), conditions.getHFunction());
            Space[] spacePath = new Space[nodePath.length];
            for (int i = 0; i < nodePath.length; i++) {
                spacePath[i] = spaces[nodePath[i].x][nodePath[i].y];
            }
            return spacePath;
        } catch (PathNotFoundException e) {
            throw e;
        }
    }

    private static Node[] getPath(Node startingNode, Node destinationNode, Node[][] grid, boolean diagonal, BiFunction<Node,Node,Double> hFunction) throws PathNotFoundException{
        PriorityQueue<Node> open = new PriorityQueue<Node>();
        List<Node> closed = new ArrayList<Node>();

        startingNode.g = 0;
        startingNode.h = hFunction.apply(startingNode, destinationNode);
        startingNode.f = startingNode.h + startingNode.g + startingNode.d;

        open.add(startingNode);
        while (!open.isEmpty()) {
            Node current = open.poll();
            closed.add(current);
            if (current.x == destinationNode.x && current.y == destinationNode.y){
                break;
            }
            List<Node> neighbors = new ArrayList<Node>();
            if (diagonal) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        getNeighbor(grid, current, neighbors, i, j);
                    }
                }
            } else {
                getNeighbor(grid, current, neighbors, 0, -1);
                getNeighbor(grid, current, neighbors, -1, 0);
                getNeighbor(grid, current, neighbors, 1, 0);
                getNeighbor(grid, current, neighbors, 0, 1);
            }
            for (Node neighborNode : neighbors) {

                Node testNode = new Node(neighborNode.x, neighborNode.y);
                testNode.parentCords = new Cords(current.x, current.y);
                testNode.g = generateG(testNode, grid);
                testNode.h = hFunction.apply(testNode, destinationNode);
                testNode.d = neighborNode.d;
                testNode.f = testNode.g + testNode.h + testNode.d;

                boolean quitByClosed = false;
                for (Node closedNode : closed) {
                    if (closedNode.x == testNode.x && closedNode.y == testNode.y){
                        quitByClosed = true;
                        break;
                    }
                }
                if (quitByClosed){
                    continue;
                }


                boolean foundInOpen = false;
                for (Node openNode : open) {
                    if (openNode.x == testNode.x && openNode.y == testNode.y){
                        if (openNode.g > testNode.g){

                            openNode.g = testNode.g;
                            openNode.h = testNode.h;
                            openNode.d = testNode.d;
                            openNode.f = testNode.f;
                            openNode.parentCords = testNode.parentCords;

                        }
                        foundInOpen = true;
                    }
                }
                if (!foundInOpen){
                    Node referencedNode = grid[testNode.x][testNode.y];
                    referencedNode.g = testNode.g;
                    referencedNode.h = testNode.h;
                    referencedNode.d = testNode.d;
                    referencedNode.f = testNode.f;
                    referencedNode.parentCords = testNode.parentCords;
                    open.add(referencedNode);
                }
            }
        }

        Stack<Node> finalPath = new Stack<Node>();
        Node fin = closed.get(closed.size()-1);
        if (!fin.equals(destinationNode)){
            throw new PathNotFoundException(destinationNode);
        }
        finalPath.add(fin);
        while (fin.parentCords != null) {
            fin = grid[fin.parentCords.x][fin.parentCords.y];
            finalPath.add(fin);
        }
        Node[] path = new Node[finalPath.size()];
        for (int i = 0; i < path.length; i++) {
            path[i] = finalPath.pop();
        }
        return path;
    }

    private static void getNeighbor(Node[][] grid, Node current, List<Node> neighbors, int i, int j) {
        if (i == 0 && j == 0){
            return;
        }
        int dx = current.x + i;
        int dy = current.y + j;
        if (dx < 0 || dx >= grid.length || dy < 0 || dy >= grid[dx].length){
            return;
        }
        Node neighbor = grid[dx][dy];
        if (!neighbor.passable){
            return;
        }
        neighbors.add(neighbor);
    }

    private static double generateH(Node from, Node to){
        return Math.abs(to.x - from.x) + Math.abs(to.y - from.y);
    }

    private static double generateG(Node current, Node[][] grid){
        return grid[current.parentCords.x][current.parentCords.y].g + 1;
    }

    private Path(){
        throw new UnsupportedOperationException("Cannot instantiate this class.");
    }
}
