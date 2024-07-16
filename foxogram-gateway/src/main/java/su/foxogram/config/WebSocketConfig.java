package su.foxogram.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import su.foxogram.controllers.message.MessageCreatedController;
import su.foxogram.controllers.user.ReadyController;
import su.foxogram.services.AuthenticationService;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    AuthenticationService authenticationService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(readyHandler(), "/ready"); //.setHandshakeHandler(handshakeHandler());
        registry.addHandler(messageCreatedHandler(), "/message-created");
    }

    private WebSocketHandler readyHandler() {
        return new ReadyController(authenticationService);
    }

    private WebSocketHandler messageCreatedHandler() {
        return new MessageCreatedController();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }

//    @Bean
//    public DefaultHandshakeHandler handshakeHandler() {
//        JettyRequestUpgradeStrategy strategy = new JettyRequestUpgradeStrategy();
//        strategy.addWebSocketConfigurer(configurable -> {
//            policy.setInputBufferSize(8192);
//            policy.setIdleTimeout(600000);
//        });
//        return new DefaultHandshakeHandler(strategy);
//    }

}