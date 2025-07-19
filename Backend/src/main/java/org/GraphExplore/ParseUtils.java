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

    public static String createSendBackString(List<int[]> explored, List<int[]> path, String status, int cost, int numExplored){
        StringBuilder sb = new StringBuilder();

        sb.append(status);
        sb.append("::");

        for(int[] exploredRC: explored){
            sb.append(exploredRC[0] + " " + exploredRC[1] + "|");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("::");

        for(int[] pathRC: path){
            sb.append(pathRC[0] + " " + pathRC[1] + "|");
        }
        if(!path.isEmpty()){
            sb.deleteCharAt(sb.length()-1);
        }
        else{
            sb.append(" ");
        }


        sb.append("::");
        sb.append(cost);

        sb.append("::");
        sb.append(numExplored);


        return sb.toString();

    }

    public static String convertRandomGridToString(String[][] stringGrid, int n){
        StringBuilder sb = new StringBuilder();

        for(int r = 0; r < n; r++){
            for(int c = 0; c < n; c++){
                sb.append(stringGrid[r][c]);
                sb.append(" ");
            }
        }

        sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }
}
