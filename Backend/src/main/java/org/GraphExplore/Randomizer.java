package org.GraphExplore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.GraphExplore.Constants.RUNS_DIVISOR;
import static org.GraphExplore.Constants.SIZE_DIVISOR;

public class Randomizer {
    private GraphAlgorithms graphAlgorithms;
    private HashMap<Integer, String> intToColour;

    public Randomizer(GraphAlgorithms graphAlgorithms){
        this.graphAlgorithms = graphAlgorithms;
        intToColour = new HashMap<>();
        intToColour.put(1, "yellow");
        intToColour.put(2, "red");
        intToColour.put(3, "blue");
    }

    public static String[][] createInitialBoard(int n){
        String[][] grid = new String[n][n];
        for(int i = 0; i < n; i++){
            Arrays.fill(grid[i], "black");
        }

        return grid;
    }

    //inclusive of min and max btw [min, max]
    public static int getRandom(int min, int max){
        return (int) (Math.random() * (max-min + 1) + min);
    }

    public static int[] getRandomStartCoord(int n) {
        // 0: top, 1: bottom, 2: left, 3: right
        int edge = getRandom(0, 3);
        return getCoordOnEdge(edge, n);
    }

    public static int[] getRandomEndCoord(int n, int[] start) {
        int startEdge = getEdgeFromCoord(start, n);
        int oppositeEdge = getOppositeEdge(startEdge);
        return getCoordOnEdge(oppositeEdge, n);
    }

    private static int[] getCoordOnEdge(int edge, int n) {
        switch(edge){
            case 0: // top
                return new int[]{0, getRandom(0, n - 1)};
            case 1: // bottom
                return new int[]{n - 1, getRandom(0, n - 1)};
            case 2: // left
                return new int[]{getRandom(0, n - 1), 0};
            case 3: // right
                return new int[]{getRandom(0, n - 1), n - 1};
            default:
                throw new IllegalArgumentException("Invalid edge");
        }
    }

    private static int getEdgeFromCoord(int[] coord, int n) {
        if (coord[0] == 0) return 0;          // top
        if (coord[0] == n - 1) return 1;      // bottom
        if (coord[1] == 0) return 2;          // left
        if (coord[1] == n - 1) return 3;      // right
        throw new IllegalArgumentException("Start coordinate is not on an edge");
    }

    private static int getOppositeEdge(int edge) {
        switch (edge) {
            case 0: return 1; // top -> bottom
            case 1: return 0; // bottom -> top
            case 2: return 3; // left -> right
            case 3: return 2; // right -> left
            default:
                throw new IllegalArgumentException("Invalid edge");
        }
    }

    public static boolean isSameCoord(int[] c1, int[] c2) {
        return c1[0] == c2[0] && c1[1] == c2[1];
    }

    public void generateSquare(String[][] grid, int n, int r, int c){
        int size = getRandom(2, 2 + n/SIZE_DIVISOR);
        String colour = intToColour.get(getRandom(1, 3));
        for(int i = r; i < r + size; i++){
            for(int j = c; j < c +size; j++){
                int row = r + i;
                int col = c + j;
                if (row >= 0 && row < n && col >= 0 && col < n && grid[row][col].equals("white")) {
                    grid[row][col] = colour;
                }
            }
        }
    }

    public void generateRectangle(String[][] grid, int n, int r, int c){
        int width = getRandom(1, 1 + n/SIZE_DIVISOR);
        int height = getRandom(1, 1 + n/SIZE_DIVISOR);
        String colour = intToColour.get(getRandom(1, 3));
        for(int i = r; i < r + height; i++){
            for(int j = c; j < c +width; j++){
                int row = r + i;
                int col = c + j;
                if (row >= 0 && row < n && col >= 0 && col < n && grid[row][col].equals("white")) {
                    grid[row][col] = colour;
                }
            }
        }
    }

    public void generateStairs(String[][] grid, int n, int r, int c){
        int size = getRandom(2, 2 + n/SIZE_DIVISOR);
        String colour = intToColour.get(getRandom(1, 3));

        for (int i = 0; i < size; i++){
            for (int j = 0; j <= i; j++){
                int row = r + i;
                int col = c + j;
                if (row >= 0 && row < n && col >= 0 && col < n && grid[row][col].equals("white")) {
                    grid[row][col] = colour;
                }
            }
        }
    }

    public void generateTriangle(String[][] grid, int n, int r, int c) {
        int size = getRandom(2, 2 + n / SIZE_DIVISOR);
        String colour = intToColour.get(getRandom(1, 3));

        for (int i = 0; i < size; i++) {
            int row = r - i;
            int startCol = c + size - 1 - i;
            int endCol = c + size - 1 + i;

            for (int col = startCol; col <= endCol; col++) {
                if (row >= 0 && row < n && col >= 0 && col < n && grid[row][col].equals("white")) {
                    grid[row][col] = colour;
                }
            }
        }
    }

    public void generateZigZag(String[][] grid, int n, int r, int c) {
        int length = getRandom(3, (int) (3 + n / (SIZE_DIVISOR/1.5)));
        int thickness = getRandom(1, Math.max(1, n / (4*SIZE_DIVISOR)));
        String colour = intToColour.get(getRandom(1, 3));

        int row = r;
        int col = c;
        boolean goingDown = true;

        for(int i = 0; i < length; i++){
            for(int t = 0; t < thickness; t++){
                int newRow;
                if (goingDown){
                    newRow = row + t;
                }
                else{
                    newRow = row - t;
                }
                int newCol = col + t;

                if(newRow >= 0 && newRow < n && newCol >= 0 && newCol < n && grid[newRow][newCol].equals("white")){
                    grid[newRow][newCol] = colour;
                }
            }

            if(goingDown){
                row = row + thickness - 1;
            }
            else{
                row = row - thickness + 1;
            }
            col += thickness;

            goingDown = !goingDown;
        }
    }

    public void pickAShape(String[][] grid, int n){
        int shapeNum = getRandom(0, 4);
        int r = getRandom(0, n-1);
        int c = getRandom(0, n-1);


        switch(shapeNum){
            case 0:
                generateSquare(grid, n, r, c);
                break;
            case 1:
                generateRectangle(grid, n, r, c);
                break;
            case 2:
                generateStairs(grid, n, r, c);
                break;
            case 3:
                generateTriangle(grid, n, r, c);
                break;
            case 4:
                generateZigZag(grid, n, r, c);
                break;
            default:
                break;
        }

    }

    //need to dos
    //add random dfs shape? limit size
    //make it so that both the min and max of number of runs and sizes depend on size n


    public String[][] getRandomGridStructured(int n) throws InterruptedException {
        String[][] grid = createInitialBoard(n);
        int min = 2;
        int max = min + n/RUNS_DIVISOR;
        int numPaths = getRandom(min, max);
        int[] start = getRandomStartCoord(n);
        int[] end = getRandomEndCoord(n, start);
        grid[start[0]][start[1]] = "orange";
        grid[end[0]][end[1]] = "purple";

        for(int i = 0; i < numPaths; i++){
            int[][] tempGrid = new int[n][n];

            tempGrid[start[0]][start[1]] = -1;
            tempGrid[end[0]][end[1]] = -2;

            List<int[]> explored = graphAlgorithms.randomDfsParent(tempGrid, "0", false);

            for(int[] coord: explored){
                if(isSameCoord(start, coord) || isSameCoord(end, coord)){
                    continue;
                }
                grid[coord[0]][coord[1]] = "white";
            }
        }

        for(int i = 0; i < getRandom(2, 2 + n/SIZE_DIVISOR); i++){
            pickAShape(grid, n);
        }

        return grid;

    }

    public String getRandomGrid(int n) throws InterruptedException {
        String[][] grid = getRandomGridStructured(n);

        return ParseUtils.convertRandomGridToString(grid, n);
    }
}
