package com.vfers.utils.websocket.handler;

import com.vfers.utils.ICallback;

/**
 * 系统消息处理器.
 * 
 * @author Letuer
 * 
 */
public class SystemMsgHandler extends DefaultWebsocketHandler {
  // @Resource(name = "systemMsgService")
  // private SystemMsgService systemMsgService

  /**
   * 构造函数.
   */
  public SystemMsgHandler() {
    setAddCallback(new ICallback() {

      @Override
      public void success(Object arg, Object... args) {
        // TODO Auto-generated method stub

      }

      @Override
      public void failed(Object arg, Object... args) {
        // TODO Auto-generated method stub

      }

      @Override
      public void exception(Object arg, Object... args) {
        // TODO Auto-generated method stub

      }
    });
  }
}
