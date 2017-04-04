package com.vfers.utils.websocket.config;

import com.vfers.utils.websocket.handler.SystemMsgHandler;
import com.vfers.utils.websocket.interceptor.DefaultInterceptor;
import com.vfers.utils.websocket.session.EnumSessionType;
import com.vfers.utils.websocket.session.SessionMgr;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebsocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

  /**
   * 服务注册.
   */
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    SystemMsgHandler handler = systemMsgHandler();
    DefaultInterceptor interceptor = new DefaultInterceptor(EnumSessionType.system_msg);
    registry.addHandler(handler, "/ws/system/msg").addInterceptors(interceptor);
    registry.addHandler(handler, "/sockjs/ws/system/msg").addInterceptors(interceptor);
  }

  /**
   * 获取系统消息ws服务Handler.
   * 
   * @return Handler
   */
  @Bean
  public SystemMsgHandler systemMsgHandler() {
    if (SessionMgr.getInstance().regist(EnumSessionType.system_msg)) {
      return new SystemMsgHandler();
    } else {
      throw new RuntimeException("Regist websocket service(" + EnumSessionType.system_msg
          + ") failed.");
    }
  }
}
