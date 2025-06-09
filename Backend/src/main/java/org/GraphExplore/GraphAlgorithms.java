package org.GraphExplore;

public class GraphAlgorithms {
    private GraphExploreWebSocketHandler webSocketHandler;

    // Constructor injection of WebSocket handler
    public GraphAlgorithms(GraphExploreWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }


    public void dijkstra(int[][] grid) throws InterruptedException {
        int n = grid.length;

    }


    // Method to send update to the frontend via WebSocket
    private void sendUpdateToFrontend(String value) {
        if (webSocketHandler != null) {
            webSocketHandler.sendUpdate(value);
            System.out.println(value);
        }
    }


}
