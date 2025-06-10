package org.GraphExplore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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
