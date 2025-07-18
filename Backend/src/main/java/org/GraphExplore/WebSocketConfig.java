package org.GraphExplore;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GraphExploreWebSocketHandler webSocketHandler;

    // Inject the singleton bean via constructor
    public WebSocketConfig(GraphExploreWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/graph_explore")
                .setAllowedOrigins(
                        "https://graph-exploration-visualiser.onrender.com",
                        "http://127.0.0.1:8081"
                );
    }

}