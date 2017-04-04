package com.vfers.utils.websocket.interceptor;

import com.vfers.utils.websocket.session.EnumSessionType;
import com.vfers.utils.websocket.session.SessionMgr;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class DefaultInterceptor implements HandshakeInterceptor {

  private EnumSessionType sessionType;

  /**
   * 构造函数.
   * 
   * @param sessionType 会话类型
   */
  public DefaultInterceptor(EnumSessionType sessionType) {
    this.sessionType = sessionType;
  }

  /**
   * 连接建立后事件.
   */
  @Override
  public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1,
      WebSocketHandler arg2, Exception arg3) {}

  /**
   * 连接建立前事件.
   */
  @Override
  public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse response,
      WebSocketHandler handler, Map<String, Object> attributes) throws Exception {
    if (serverHttpRequest instanceof ServletServerHttpRequest) {
      HttpServletRequest request =
          ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
      SessionMgr.getInstance().appendAttr(attributes, sessionType, request);
    }
    return false;
  }


}
