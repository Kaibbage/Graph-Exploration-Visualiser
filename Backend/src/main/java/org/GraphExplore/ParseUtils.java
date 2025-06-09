package org.GraphExplore;

import java.util.List;

public class ParseUtils {
    public static int[][] getGridFromString(String input){
        String[] splitTwo = input.split("::");

        int n = Integer.parseInt(splitTwo[0]);

        int[][] grid = new int[n][n];

        String[] splitRows = splitTwo[1].split(" \\| ");

        System.out.println(splitRows.length);

        for(int r = 0; r < n; r++){
            String[] splitUnits = splitRows[r].split(" ");
            for(int c = 0; c < n; c++){
                grid[r][c] = Integer.parseInt(splitUnits[c]);
            }
        }

        return grid;
    }

    public static String createSendBackString(List<int[]> explored, List<int[]> path, boolean done){
        StringBuilder sb = new StringBuilder();

        return "";

    }
}
