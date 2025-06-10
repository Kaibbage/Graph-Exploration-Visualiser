package org.GraphExplore;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import org.GraphExplore.ParseUtils;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:8081")  // Adjust the URL to your frontend's URL if necessary
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



    @PostMapping("/start-solving")
    public String startSolving(@RequestBody InputRequest request) {
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




    // Method to send update to the frontend via WebSocket
    private void sendUpdateToFrontend(String value) {
        if (webSocketHandler != null) {
            webSocketHandler.sendUpdate(value);
        }
    }


}
