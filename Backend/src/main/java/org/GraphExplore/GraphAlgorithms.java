package org.GraphExplore;

import java.util.*;

import static org.GraphExplore.Constants.TIME;
import static org.GraphExplore.Constants.directions;
import static org.GraphExplore.ParseUtils.createSendBackString;

public class GraphAlgorithms {
    private GraphExploreWebSocketHandler webSocketHandler;

    //giving websocket to here so it can send to frontend
    public GraphAlgorithms(GraphExploreWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }


    public void dijkstra(int[][] grid, String id) throws InterruptedException {
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
        int finalCost = 0;

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

            buildAndSendString(explored, current.path, "inProgress", current.length, explored.size(), n, id);

            if(r == end[0] && c == end[1]){
                solved = true;
                finalPath = current.path;
                finalCost = current.length;
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
            buildAndSendString(explored, finalPath, "solved", finalCost, explored.size(), n, id);
        }
        else{
            buildAndSendString(explored, finalPath, "failed", finalCost, explored.size(), n, id);
        }

    }



    public void astar(int[][] grid, String id) throws InterruptedException {
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
        int finalCost = 0;

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

            buildAndSendString(explored, current.path, "inProgress", current.length, explored.size(), n, id);

            if(r == end[0] && c == end[1]){
                //we did it!
                solved = true;
                finalPath = current.path;
                finalCost = current.length;
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
            buildAndSendString(explored, finalPath, "solved", finalCost, explored.size(), n, id);
        }
        else{
            buildAndSendString(explored, finalPath, "failed", finalCost, explored.size(), n, id);
        }
    }

    public void bfs(int[][] grid, String id) throws InterruptedException {
        int n = grid.length;

        int[] start = new int[2];
        int[] end = new int[2];

        getStartAndEnd(start, end, grid, n);

        List<int[]> explored = new ArrayList<>();
        boolean[][] seen = new boolean[n][n];

        boolean solved = false;
        List<int[]> finalPath = new ArrayList<>();
        int finalCost = 0;

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

            buildAndSendString(explored, current.path, "inProgress", current.length, explored.size(), n, id);

            if(r == end[0] && c == end[1]){
                //we did it!
                solved = true;
                finalPath = current.path;
                finalCost = current.length;
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
            buildAndSendString(explored, finalPath, "solved", finalCost, explored.size(), n, id);
        }
        else{
            buildAndSendString(explored, finalPath, "failed", finalCost, explored.size(), n, id);
        }
    }



    public void dfsParent(int[][] grid, String id) throws InterruptedException {
        int n = grid.length;

        int[] start = new int[2];
        int[] end = new int[2];
        getStartAndEnd(start, end, grid, n);

        List<int[]> explored = new ArrayList<>();
        boolean[][] seen = new boolean[n][n];

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 10_000) {
                    seen[r][c] = true;
                }
            }
        }

        List<int[]> finalPath = new ArrayList<>();
        int[] finalCost = new int[1];

        boolean solved = iterativeDfs(grid, start, end, seen, explored, finalPath, finalCost, id, n);

        if (solved) {
            buildAndSendString(explored, finalPath, "solved", finalCost[0], explored.size(), n, id);
        } else {
            buildAndSendString(explored, finalPath, "failed", finalCost[0], explored.size(), n, id);
        }
    }

    public boolean iterativeDfs(int[][] grid, int[] start, int[] end, boolean[][] seen,
                                List<int[]> explored, List<int[]> finalPath, int[] finalCost,
                                String id, int n) throws InterruptedException {

        Stack<LengthPathPosition> stack = new Stack<>();
        stack.push(new LengthPathPosition(0, new ArrayList<>(), start[0], start[1]));

        while (!stack.isEmpty()) {
            LengthPathPosition current = stack.pop();
            int r = current.r;
            int c = current.c;

            if (seen[r][c]) continue;
            seen[r][c] = true;
            explored.add(new int[]{r, c});

            buildAndSendString(explored, current.path, "inProgress", current.length, explored.size(), n, id);

            if (r == end[0] && c == end[1]) {
                finalPath.addAll(current.path);
                finalCost[0] = current.length;
                return true;
            }

            for (int i = directions.length - 1; i >= 0; i--) { // Reverse order to maintain DFS direction order
                int[] direction = directions[i];
                int nr = r + direction[0];
                int nc = c + direction[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc]) {
                    List<int[]> newPath = new ArrayList<>(current.path);
                    newPath.add(new int[]{r, c});
                    stack.push(new LengthPathPosition(current.length + grid[nr][nc], newPath, nr, nc));
                }
            }
        }

        return false;
    }


    // Entry point
    public List<int[]> randomDfsParent(int[][] grid, String id, boolean real) throws InterruptedException {
        int n = grid.length;

        int[] start = new int[2];
        int[] end = new int[2];
        getStartAndEnd(start, end, grid, n);

        List<int[]> explored = new ArrayList<>();
        boolean[][] seen = new boolean[n][n];

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 10_000) {
                    seen[r][c] = true;
                }
            }
        }

        List<int[]> finalPath = new ArrayList<>();
        int[] finalCost = new int[1];

        boolean solved = iterativeRandomDfs(grid, start, end, seen, explored, finalPath, finalCost, id, real, n);

        if (real) {
            if (solved) {
                buildAndSendString(explored, finalPath, "solved", finalCost[0], explored.size(), n, id);
            } else {
                buildAndSendString(explored, finalPath, "failed", finalCost[0], explored.size(), n, id);
            }
        }

        return finalPath;
    }


    public boolean iterativeRandomDfs(int[][] grid, int[] start, int[] end, boolean[][] seen,
                                      List<int[]> explored, List<int[]> finalPath, int[] finalCost,
                                      String id, boolean real, int n) throws InterruptedException {

        Stack<LengthPathPosition> stack = new Stack<>();
        stack.push(new LengthPathPosition(0, new ArrayList<>(), start[0], start[1]));

        while (!stack.isEmpty()) {
            LengthPathPosition current = stack.pop();
            int r = current.r;
            int c = current.c;

            if (seen[r][c]) continue;
            seen[r][c] = true;

            explored.add(new int[]{r, c});

            if (real) {
                buildAndSendString(explored, current.path, "inProgress", current.length, explored.size(), n, id);
            }

            if (r == end[0] && c == end[1]) {
                finalPath.addAll(current.path);
                finalCost[0] = current.length;
                return true;
            }

            // Create a fresh shuffled direction list
            List<int[]> directionList = new ArrayList<>();
            for (int[] direction : directions) {
                directionList.add(new int[]{direction[0], direction[1]});
            }
            Collections.shuffle(directionList);

            for (int[] direction : directionList) {
                int nr = r + direction[0];
                int nc = c + direction[1];

                if (nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc]) {
                    List<int[]> newPath = new ArrayList<>(current.path);
                    newPath.add(new int[]{r, c});
                    stack.push(new LengthPathPosition(current.length + grid[nr][nc], newPath, nr, nc));
                }
            }
        }

        return false;
    }






    //estimate for astar, can be changed maybe, perhaps should average cost of path so far and multiply, or maybe avg cost of graph
    public int getEstimate(int[] pos, int[] end){
        int r = pos[0], c = pos[1];
        int rEnd = end[0], cEnd = end[1];
        return Math.abs(r - rEnd) + Math.abs(c - cEnd);
    }

    //time slept should maybe be changed based on size of grid? maybe also slower so easier to see
    public void buildAndSendString(List<int[]> explored, List<int[]> path, String status, int cost, int numExplored, int n, String id) throws InterruptedException {
        Thread.sleep(TIME/(n*n));
        String s = createSendBackString(explored, path, status, cost, numExplored);
        sendUpdateToFrontend(s, id);
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
    private void sendUpdateToFrontend(String value, String id) {
        if (webSocketHandler != null) {
            webSocketHandler.sendUpdate(value, id);
        }
    }

    //perhaps better version of astar, but for my usage here i don't want to revisit nodes, this would get a better path in some scenarios
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


    //recursive randomdfs
//    public List<int[]> randomDfsParent(int[][] grid, String id, boolean real) throws InterruptedException {
//        int n = grid.length;
//
//        int[] start = new int[2];
//        int[] end = new int[2];
//
//        getStartAndEnd(start, end, grid, n);
//
//        List<int[]> explored = new ArrayList<>();
//        boolean[][] seen = new boolean[n][n];
//
//        boolean solved = false;
//        List<int[]> finalPath = new ArrayList<>();
//        int[] finalCost = new int[1];
//
//        for(int r = 0; r < n; r++){
//            for(int c = 0; c < n; c++){
//                if(grid[r][c] == 10_000){
//                    seen[r][c] = true;
//                }
//            }
//        }
//
//        solved = randomDfs(grid, new LengthPathPosition(0, new ArrayList<>(), start[0], start[1]), explored, seen, finalPath, end, n, finalCost, id, real);
//
//        if(real){
//            if(solved){
//                buildAndSendString(explored, finalPath, "solved", finalCost[0], explored.size(), n, id);
//            }
//            else{
//                buildAndSendString(explored, finalPath, "failed", finalCost[0], explored.size(), n, id);
//            }
//        }
//
//        return finalPath;
//
//    }
//
//    public boolean randomDfs(int[][] grid, LengthPathPosition current, List<int[]> explored, boolean[][] seen, List<int[]> finalPath, int[] end, int n, int[] finalCost, String id, boolean real) throws InterruptedException {
//        int r = current.r;
//        int c = current.c;
//
//
//        seen[r][c] = true;
//        explored.add(new int[]{r, c});
//
//        if(real){
//            buildAndSendString(explored, current.path, "inProgress", current.length, explored.size(), n, id);
//        }
//
//
//        if(r == end[0] && c == end[1]){
//            //we did it!
//            finalPath.addAll(current.path);
//            finalCost[0] = current.length;
//            return true;
//        }
//
//        List<int[]> directionList = new ArrayList<>();
//        for(int[] direction: directions){
//            directionList.add(new int[]{direction[0], direction[1]});
//        }
//        Collections.shuffle(directionList);
//
//        for(int[] direction: directionList){
//            int nr = r + direction[0];
//            int nc = c + direction[1];
//
//            if(nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc]){
//                LengthPathPosition next = new LengthPathPosition(current.length + grid[nr][nc], current.path, nr, nc);
//                if(randomDfs(grid, next, explored, seen, finalPath, end, n, finalCost, id, real)){
//                    return true;
//                }
//            }
//        }
//
//
//        return false;
//    }

    //recursive normal dfs
//    public void dfsParent(int[][] grid, String id) throws InterruptedException {
//        int n = grid.length;
//
//        int[] start = new int[2];
//        int[] end = new int[2];
//
//        getStartAndEnd(start, end, grid, n);
//
//        List<int[]> explored = new ArrayList<>();
//        boolean[][] seen = new boolean[n][n];
//
//        boolean solved = false;
//        List<int[]> finalPath = new ArrayList<>();
//        int[] finalCost = new int[1];
//
//        for(int r = 0; r < n; r++){
//            for(int c = 0; c < n; c++){
//                if(grid[r][c] == 10_000){
//                    seen[r][c] = true;
//                }
//            }
//        }
//
//        solved = dfs(grid, new LengthPathPosition(0, new ArrayList<>(), start[0], start[1]), explored, seen, finalPath, end, n, finalCost, id);
//
//        if(solved){
//            buildAndSendString(explored, finalPath, "solved", finalCost[0], explored.size(), n, id);
//        }
//        else{
//            buildAndSendString(explored, finalPath, "failed", finalCost[0], explored.size(), n, id);
//        }
//
//
//    }
//
//    public boolean dfs(int[][] grid, LengthPathPosition current, List<int[]> explored, boolean[][] seen, List<int[]> finalPath, int[] end, int n, int[] finalCost, String id) throws InterruptedException {
//        int r = current.r;
//        int c = current.c;
//
//
//        seen[r][c] = true;
//        explored.add(new int[]{r, c});
//
//        buildAndSendString(explored, current.path, "inProgress", current.length, explored.size(), n, id);
//
//        if(r == end[0] && c == end[1]){
//            //we did it!
//            finalPath.addAll(current.path);
//            finalCost[0] = current.length;
//            return true;
//        }
//
//        for(int[] direction: directions){
//            int nr = r + direction[0];
//            int nc = c + direction[1];
//
//            if(nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc]){
//                LengthPathPosition next = new LengthPathPosition(current.length + grid[nr][nc], current.path, nr, nc);
//                if(dfs(grid, next, explored, seen, finalPath, end, n, finalCost, id)){
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }


}
