package org.GraphExplore;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import org.GraphExplore.ParseUtils;

@RestController
@CrossOrigin(origins = {
        "https://graph-exploration-visualiser.onrender.com",
        "http://127.0.0.1:8081"
})
public class GraphExploreController {

    private GraphExploreWebSocketHandler webSocketHandler;
    private GraphAlgorithms graphAlgorithms;

    // Constructor injection of WebSocket handler
    public GraphExploreController(GraphExploreWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        graphAlgorithms = new GraphAlgorithms(webSocketHandler);
    }

    public static class InputRequest {
        private String input;

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }
    }


    @GetMapping("/")
    public String home() {
        return "Graph Explore Backend is running!";
    }

    @PostMapping("/start-solving-dijkstra")
    public String startSolvingDijkstra(@RequestBody InputRequest request) {
        String input = request.getInput();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.dijkstra(grid);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }


    @PostMapping("/start-solving-astar")
    public String startSolvingAstar(@RequestBody InputRequest request) {
        String input = request.getInput();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.astar(grid);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }

    @PostMapping("/start-solving-bfs")
    public String startSolvingBFS(@RequestBody InputRequest request) {
        String input = request.getInput();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.bfs(grid);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }

    @PostMapping("/start-solving-dfs")
    public String startSolvingDFS(@RequestBody InputRequest request) {
        String input = request.getInput();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.dfsParent(grid);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }

    @PostMapping("/start-solving-randomdfs")
    public String startSolvingRandomDFS(@RequestBody InputRequest request) {
        String input = request.getInput();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.randomDfsParent(grid);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }



}
