package com.vfers.utils.websocket.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * WebSocket客户端.
 * 
 * @author Letuer
 * 
 */
@ClientEndpoint
public class WebsocketClient {

  /**
   * 构造函数.
   * 
   * @param url websocket url : wss:// | ws://
   * @throws URISyntaxException url非法
   * @throws IOException IO异常
   * @throws DeploymentException 部署异常
   */
  public WebsocketClient(String url) throws DeploymentException, IOException, URISyntaxException {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    container.setDefaultMaxTextMessageBufferSize(16384);
    container.setDefaultMaxBinaryMessageBufferSize(16384);
    container.connectToServer(this, new URI(url));
  }

  @OnOpen
  public void onOpen() {

  }

  @OnClose
  public void onClose() {

  }

  @OnMessage
  public void onMessage(String message, Session session) {

  }
}
