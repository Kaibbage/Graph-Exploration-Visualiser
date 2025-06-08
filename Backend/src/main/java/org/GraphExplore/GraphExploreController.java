package org.GraphExplore;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:8081")  // Adjust the URL to your frontend's URL if necessary
public class GraphExploreController {

    private GraphExploreWebSocketHandler webSocketHandler;

    // Constructor injection of WebSocket handler
    public GraphExploreController(GraphExploreWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
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



//    // Endpoint to start the recursive counting process
//    @PostMapping("/start-solving")
//    public String startSolving(@RequestBody InputRequest request) {
//        String input = request.getInput();
//        System.out.println(input);
//
//        char[][] grid = getGridFromString(input);
//        new Thread(() -> {
//            try {
//                solveSudoku(grid);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//
//
//        return "Sudoku solving started";
//    }
//
//    @PostMapping("/generate-random")
//    public String generateRandom() {
//        int numNumbers = ((int) (Math.random() * 30)) + 10;
//
//        System.out.println(numNumbers);
//
//        char[][] grid = generateRandomGrid(numNumbers);
//
//
//        String stringGrid = getStringFromGrid(grid);
//
//        return stringGrid;
//    }


    // Method to send the counter update to the frontend via WebSocket
    private void sendUpdateToFrontend(String value) {
        if (webSocketHandler != null) {
            webSocketHandler.sendUpdate(value);
            System.out.println(value);
        }
    }


}
