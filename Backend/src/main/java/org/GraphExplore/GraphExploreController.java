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
    private Randomizer randomizer;

    // Constructor injection of WebSocket handler
    public GraphExploreController(GraphExploreWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        graphAlgorithms = new GraphAlgorithms(webSocketHandler);
        randomizer = new Randomizer(graphAlgorithms);
    }

    public static class InputRequest {
        private String input;
        private String id;

        public String getInput() {
            return input;
        }
        public String getId(){
            return id;
        }

        public void setInput(String input) {
            this.input = input;
        }
        public void setId(String id){
            this.id = id;
        }
    }


    @GetMapping("/")
    public String home() {
        return "Graph Explore Backend is running!";
    }

    @PostMapping("/start-solving-dijkstra")
    public String startSolvingDijkstra(@RequestBody InputRequest request) {
        String input = request.getInput();
        String id = request.getId();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.dijkstra(grid, id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }


    @PostMapping("/start-solving-astar")
    public String startSolvingAstar(@RequestBody InputRequest request) {
        String input = request.getInput();
        String id = request.getId();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.astar(grid, id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }

    @PostMapping("/start-solving-bfs")
    public String startSolvingBFS(@RequestBody InputRequest request) {
        String input = request.getInput();
        String id = request.getId();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.bfs(grid, id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }

    @PostMapping("/start-solving-dfs")
    public String startSolvingDFS(@RequestBody InputRequest request) {
        String input = request.getInput();
        String id = request.getId();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.dfsParent(grid, id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }

    @PostMapping("/start-solving-randomdfs")
    public String startSolvingRandomDFS(@RequestBody InputRequest request) {
        String input = request.getInput();
        String id = request.getId();

        int[][] grid = ParseUtils.getGridFromString(input);

        new Thread(() -> {
            try {
                graphAlgorithms.randomDfsParent(grid, id, true);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        return "Shortest Path solving started";
    }

    @PostMapping("/get-random-grid")
    public String getRandomGrid(@RequestBody InputRequest request) throws InterruptedException {
        int n = Integer.parseInt(request.getInput());

        String result = randomizer.getRandomGrid(n);

        return result;

    }



}
