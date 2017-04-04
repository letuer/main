package com.vfers.utils.websocket.handler;

import com.vfers.utils.ICallback;
import com.vfers.utils.websocket.session.SessionMgr;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class DefaultWebsocketHandler extends TextWebSocketHandler {

  private SessionMgr sessionMgr = SessionMgr.getInstance();

  private ICallback addCallback;

  private ICallback errorCallback;

  private ICallback closeCallback;

  /**
   * 连接关闭事件.
   */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    sessionMgr.removeSession(session, closeCallback);
    super.afterConnectionClosed(session, status);
  }

  /**
   * 连接建立事件.
   */
  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    sessionMgr.addSession(session, addCallback);
    super.afterConnectionEstablished(session);
  }

  /**
   * 消息处理.
   */
  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> msg) throws Exception {
    super.handleMessage(session, msg);
  }

  /**
   * 传输错误事件.
   */
  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    sessionMgr.closeSession(session, errorCallback);
    super.handleTransportError(session, exception);
  }

  /**
   * 是否支持局部消息.
   */
  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

  public void setAddCallback(ICallback addCallback) {
    this.addCallback = addCallback;
  }

  public void setErrorCallback(ICallback errorCallback) {
    this.errorCallback = errorCallback;
  }

  public void setCloseCallback(ICallback closeCallback) {
    this.closeCallback = closeCallback;
  }
}
