package org.GraphExplore;

import java.util.*;

import static org.GraphExplore.Constants.directions;
import static org.GraphExplore.ParseUtils.createSendBackString;

public class GraphAlgorithms {
    private GraphExploreWebSocketHandler webSocketHandler;

    // Constructor injection of WebSocket handler
    public GraphAlgorithms(GraphExploreWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }


    public void dijkstra(int[][] grid) throws InterruptedException {
        int n = grid.length;

        int[] start = new int[2];
        int[] end = new int[2];

        getStartAndEnd(start, end, grid, n);

        PriorityQueue<LengthPathPosition> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a.length));
        minHeap.add(new LengthPathPosition(0, new ArrayList<>(), start[0], start[1])); //starting at start

        List<int[]> explored = new ArrayList<>();
        boolean[][] seen = new boolean[n][n];

        boolean solved = false;
        List<int[]> finalPath = new ArrayList<>();

        for(int r = 0; r < n; r++){
            for(int c = 0; c < n; c++){
                if(grid[r][c] == 10_000){
                    seen[r][c] = true;
                }
            }
        }

        while(!minHeap.isEmpty()){
            LengthPathPosition current = minHeap.poll();
            int r = current.r;
            int c = current.c;

            if(seen[r][c]) {
                continue;
            }

            seen[r][c] = true;
            explored.add(new int[]{r, c});

            buildAndSendString(explored, current.path, false);

            if(r == end[0] && c == end[1]){
                //we did it!
                solved = true;
                finalPath = current.path;
                break;
            }

            for(int[] direction: directions){
                int nr = r + direction[0];
                int nc = c + direction[1];

                if(nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc]){
                    minHeap.add(new LengthPathPosition(current.length + grid[nr][nc], current.path, nr, nc));
                }
            }
        }

        if(solved){
            buildAndSendString(explored, finalPath, true);
        }
        else{
            //do smth maybe
        }

    }



    public void astar(int[][] grid) throws InterruptedException {
        int n = grid.length;

        int[] start = new int[2];
        int[] end = new int[2];

        getStartAndEnd(start, end, grid, n);

        PriorityQueue<LengthPathPositionEstimate> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a.length + a.estimate));
        minHeap.add(new LengthPathPositionEstimate(0, new ArrayList<>(), start[0], start[1], getEstimate(start, end))); //starting at start

        List<int[]> explored = new ArrayList<>();
        boolean[][] seen = new boolean[n][n];

        boolean solved = false;
        List<int[]> finalPath = new ArrayList<>();

        for(int r = 0; r < n; r++){
            for(int c = 0; c < n; c++){
                if(grid[r][c] == 10_000){
                    seen[r][c] = true;
                }
            }
        }

        while(!minHeap.isEmpty()){
            LengthPathPositionEstimate current = minHeap.poll();
            int r = current.r;
            int c = current.c;

            if(seen[r][c]){
                continue;
            }
            seen[r][c] = true;

            explored.add(new int[]{r, c});

            buildAndSendString(explored, current.path, false);

            if(r == end[0] && c == end[1]){
                //we did it!
                solved = true;
                finalPath = current.path;
                break;
            }

            for(int[] direction: directions){
                int nr = r + direction[0];
                int nc = c + direction[1];

                if(nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc]){
                    minHeap.add(new LengthPathPositionEstimate(current.length + grid[nr][nc], current.path, nr, nc, getEstimate(new int[]{nr, nc}, end)));
                }
            }
        }

        if(solved){
            buildAndSendString(explored, finalPath, true);
        }
        else{
            //do smth maybe
        }
    }

    public void bfs(int[][] grid) throws InterruptedException {
        int n = grid.length;

        int[] start = new int[2];
        int[] end = new int[2];

        getStartAndEnd(start, end, grid, n);

        List<int[]> explored = new ArrayList<>();
        boolean[][] seen = new boolean[n][n];

        boolean solved = false;
        List<int[]> finalPath = new ArrayList<>();

        Queue<LengthPathPosition> q = new LinkedList<>();
        q.add(new LengthPathPosition(0, new ArrayList<>(), start[0], start[1]));
        seen[start[0]][start[1]] = true;

        for(int r = 0; r < n; r++){
            for(int c = 0; c < n; c++){
                if(grid[r][c] == 10_000){
                    seen[r][c] = true;
                }
            }
        }

        while(!q.isEmpty()){
            LengthPathPosition current = q.poll();

            int r = current.r;
            int c = current.c;

            explored.add(new int[]{r, c});

            buildAndSendString(explored, current.path, false);

            if(r == end[0] && c == end[1]){
                //we did it!
                solved = true;
                finalPath = current.path;
                break;
            }

            for(int[] direction: directions){
                int nr = r + direction[0];
                int nc = c + direction[1];

                if(nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc]){
                    seen[nr][nc] = true;
                    q.add(new LengthPathPosition(current.length + grid[nr][nc], current.path, nr, nc));
                }
            }


        }

        if(solved){
            buildAndSendString(explored, finalPath, true);
        }
        else{
            //do smth maybe
        }
    }


    public void dfsParent(int[][] grid) throws InterruptedException {
        int n = grid.length;

        int[] start = new int[2];
        int[] end = new int[2];

        getStartAndEnd(start, end, grid, n);

        List<int[]> explored = new ArrayList<>();
        boolean[][] seen = new boolean[n][n];

        boolean solved = false;
        List<int[]> finalPath = new ArrayList<>();

        for(int r = 0; r < n; r++){
            for(int c = 0; c < n; c++){
                if(grid[r][c] == 10_000){
                    seen[r][c] = true;
                }
            }
        }

        solved = dfs(grid, new LengthPathPosition(0, new ArrayList<>(), start[0], start[1]), explored, seen, finalPath, end, n);

        if(solved){
            buildAndSendString(explored, finalPath, true);
        }
        else{
            //do smth maybe
        }


    }

    public boolean dfs(int[][] grid, LengthPathPosition current, List<int[]> explored, boolean[][] seen, List<int[]> finalPath, int[] end, int n) throws InterruptedException {
        int r = current.r;
        int c = current.c;


        seen[r][c] = true;
        explored.add(new int[]{r, c});

        buildAndSendString(explored, current.path, false);

        if(r == end[0] && c == end[1]){
            //we did it!
            finalPath.addAll(current.path);
            return true;
        }

        for(int[] direction: directions){
            int nr = r + direction[0];
            int nc = c + direction[1];

            if(nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc]){
                LengthPathPosition next = new LengthPathPosition(current.length + grid[nr][nc], current.path, nr, nc);
                if(dfs(grid, next, explored, seen, finalPath, end, n)){
                    return true;
                }
            }
        }

        return false;
    }

    //perhaps better version of astar, but for my usage here i don't want to revisit nodes
//    public void astar(int[][] grid) throws InterruptedException {
//        int n = grid.length;
//
//        int[] start = new int[2];
//        int[] end = new int[2];
//
//        getStartAndEnd(start, end, grid, n);
//
//        PriorityQueue<LengthPathPositionEstimate> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a.length + a.estimate));
//        minHeap.add(new LengthPathPositionEstimate(0, new ArrayList<>(), start[0], start[1], getEstimate(start, end))); //starting at start
//
//        List<int[]> explored = new ArrayList<>();
//        int[][] bestLength = new int[n][n];
//        for (int[] row : bestLength){
//            Arrays.fill(row, Integer.MAX_VALUE);
//        }
//        bestLength[start[0]][start[1]] = 0;
//
//        boolean solved = false;
//        List<int[]> finalPath = new ArrayList<>();
//
//        while(!minHeap.isEmpty()){
//            LengthPathPositionEstimate current = minHeap.poll();
//            int r = current.r;
//            int c = current.c;
//
//            explored.add(new int[]{r, c});
//
//            buildAndSendString(explored, current.path, false);
//
//            if(r == end[0] && c == end[1]){
//                //we did it!
//                solved = true;
//                finalPath = current.path;
//                break;
//            }
//
//            for(int[] direction: directions){
//                int nr = r + direction[0];
//                int nc = c + direction[1];
//
//                if(nr >= 0 && nr < n && nc >= 0 && nc < n && current.length + grid[nr][nc] < bestLength[nr][nc]){
//                    bestLength[nr][nc] = current.length + grid[nr][nc];
//                    minHeap.add(new LengthPathPositionEstimate(current.length + grid[nr][nc], current.path, nr, nc, getEstimate(new int[]{nr, nc}, end)));
//                }
//            }
//        }
//
//        if(solved){
//            buildAndSendString(explored, finalPath, true);
//        }
//        else{
//            //do smth maybe
//        }
//    }

    public int getEstimate(int[] pos, int[] end){
        int r = pos[0], c = pos[1];
        int rEnd = end[0], cEnd = end[1];
        return Math.abs(r - rEnd) + Math.abs(c - cEnd);
    }

    public void buildAndSendString(List<int[]> explored, List<int[]> path, boolean done) throws InterruptedException {
        Thread.sleep(10);
        String s = createSendBackString(explored, path, done);
        sendUpdateToFrontend(s);
    }

    public void getStartAndEnd(int[] start, int[] end, int[][] grid, int n){
        for(int r = 0; r < n; r++){
            for(int c = 0; c < n; c++){
                if(grid[r][c] == -1){
                    start[0] = r;
                    start[1] = c;
                    grid[r][c] = 0;
                }
                else if(grid[r][c] == -2){
                    end[0] = r;
                    end[1] = c;
                    grid[r][c] = 0;
                }
            }
        }
    }


    // Method to send update to the frontend via WebSocket
    private void sendUpdateToFrontend(String value) {
        if (webSocketHandler != null) {
            webSocketHandler.sendUpdate(value);
        }
    }


}
