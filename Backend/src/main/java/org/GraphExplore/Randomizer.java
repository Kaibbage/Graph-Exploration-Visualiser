package org.GraphExplore;

import java.util.Arrays;
import java.util.List;

public class Randomizer {
    private GraphAlgorithms graphAlgorithms;

    public Randomizer(GraphAlgorithms graphAlgorithms){
        this.graphAlgorithms = graphAlgorithms;
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

    public static int[] getRandomStartCoord(int n){
        return new int[]{getRandom(0, n-1), getRandom(0, n-1)};
    }

    public static int[] getRandomEndCoord(int n, int[] start){
        int[] end = new int[]{getRandom(0, n-1), getRandom(0, n-1)};
        while(isSameCoord(start, end)){
            end[0] = getRandom(0, n-1);
            end[1] = getRandom(0, n-1);
        }
        return end;
    }

    public static boolean isSameCoord(int[] c1, int[] c2){
        return c1[0] == c2[0] && c1[1] == c2[1];
    }

    public String[][] getRandomGridStructured(int n) throws InterruptedException {
        String[][] grid = createInitialBoard(n);
        int min = 1;
        int max = min + n/10;
        int numPaths = getRandom(min, max);
//        int[] start = getRandomStartCoord(n);
//        int[] end = getRandomEndCoord(n, start);
        int[] start = new int[]{0, 0};
        int[] end = new int[]{n-1, n-1};
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

        return grid;

    }

    public String getRandomGrid(int n) throws InterruptedException {
        String[][] grid = getRandomGridStructured(n);

        return ParseUtils.convertRandomGridToString(grid, n);
    }
}
